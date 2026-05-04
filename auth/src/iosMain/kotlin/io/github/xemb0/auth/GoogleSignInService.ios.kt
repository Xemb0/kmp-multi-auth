package io.github.xemb0.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * iOS Google sign-in via ASWebAuthenticationSession.
 *
 * Apple App Review Guideline 4 forbids handing the user off to the system
 * Safari app for sign-in. ASWebAuthenticationSession presents the OAuth
 * page in an in-app sheet, shares cookies with system Safari (so existing
 * Google sessions still work), and returns the callback URL directly to
 * the app — no `UIApplication.openURL` and no app switch.
 *
 * Flow:
 * 1) Build the Supabase OAuth URL (no browser open).
 * 2) Present it via ASWebAuthenticationSession; user signs in inside the sheet.
 * 3) On completion, hand the callback URL to Supabase to exchange the auth
 *    code for a session.
 * 4) Wait for `sessionStatus` to flip to `Authenticated` so the caller's
 *    spinner has a definitive completion signal.
 */
actual class GoogleSignInService(private val supabaseClient: SupabaseClient) {

    actual fun setActivityContext(context: Any) {
        // No-op on iOS — kept for API parity with Android.
    }

    actual suspend fun signIn(): GoogleSignInResult {
        return try {
            log("Building OAuth URL (scheme=${AuthConfig.deepLinkScheme}, host=${AuthConfig.deepLinkHost})")
            val authUrl = supabaseClient.auth.getOAuthUrl(provider = Google)
            log("OAuth URL: $authUrl")

            val callbackUrl = launchASWebAuthSession(
                authUrl = authUrl,
                callbackScheme = AuthConfig.deepLinkScheme,
            ) ?: run {
                log("ASWebAuthenticationSession returned null (user cancelled)")
                return GoogleSignInResult.Cancelled
            }
            log("ASWebAuthenticationSession callback URL: ${callbackUrl.absoluteString}")

            // Hand the OAuth callback URL to Supabase. This kicks off an
            // async code-for-session exchange; the new session is published
            // through `auth.sessionStatus` once the exchange completes.
            supabaseClient.handleDeeplinks(callbackUrl)
            log("handleDeeplinks invoked, waiting for session…")

            try {
                withTimeout(SESSION_WAIT_TIMEOUT_MS) {
                    supabaseClient.auth.sessionStatus
                        .first {
                            log("sessionStatus emitted: ${it::class.simpleName}")
                            it is SessionStatus.Authenticated
                        }
                }
                log("Session is now Authenticated")
            } catch (_: TimeoutCancellationException) {
                log("Timed out waiting for Authenticated session")
                return GoogleSignInResult.Error(
                    "Sign-in timed out waiting for Supabase session."
                )
            }

            // Session is live. Returning BrowserFlowStarted preserves the
            // existing UI contract: AuthScreen's sessionStatus collector
            // performs navigation, and the button's branch for this result
            // clears its local spinner.
            GoogleSignInResult.BrowserFlowStarted
        } catch (e: Exception) {
            log("signIn() exception: ${e::class.simpleName}: ${e.message}")
            GoogleSignInResult.Error(e.message ?: "Google sign-in failed")
        }
    }

    private fun log(message: String) {
        platform.Foundation.NSLog("[KmpAuth][GoogleSignInService] $message")
    }
}

private const val SESSION_WAIT_TIMEOUT_MS = 20_000L

@OptIn(ExperimentalForeignApi::class)
private suspend fun launchASWebAuthSession(
    authUrl: String,
    callbackScheme: String,
): NSURL? = withContext(Dispatchers.Main) {
    suspendCancellableCoroutine { cont ->
        val nsAuthUrl = NSURL(string = authUrl) ?: run {
            cont.resumeWithException(IllegalArgumentException("Invalid OAuth URL: $authUrl"))
            return@suspendCancellableCoroutine
        }

        val anchorProvider = PresentationAnchorProvider()
        val session = ASWebAuthenticationSession(
            uRL = nsAuthUrl,
            callbackURLScheme = callbackScheme,
            completionHandler = { callbackUrl: NSURL?, error: NSError? ->
                when {
                    // ASWebAuthenticationSessionErrorCodeCanceledLogin = 1
                    error != null && error.code == 1L -> cont.resume(null)
                    error != null -> {
                        cont.resumeWithException(RuntimeException(error.localizedDescription))
                    }
                    else -> cont.resume(callbackUrl)
                }
            }
        )
        session.presentationContextProvider = anchorProvider
        session.prefersEphemeralWebBrowserSession = false

        cont.invokeOnCancellation { session.cancel() }

        if (!session.start()) {
            cont.resumeWithException(RuntimeException("Failed to start ASWebAuthenticationSession"))
        }
    }
}

private class PresentationAnchorProvider :
    NSObject(),
    ASWebAuthenticationPresentationContextProvidingProtocol {

    override fun presentationAnchorForWebAuthenticationSession(
        session: ASWebAuthenticationSession
    ): ASPresentationAnchor {
        val windows = UIApplication.sharedApplication.windows
        val keyWindow = windows.firstOrNull { window ->
            (window as? UIWindow)?.isKeyWindow() == true
        } as? UIWindow
        return keyWindow ?: (windows.first() as UIWindow)
    }
}
