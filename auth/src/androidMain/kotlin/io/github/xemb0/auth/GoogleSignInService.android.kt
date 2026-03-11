package io.github.xemb0.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.lang.ref.WeakReference
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google

actual class GoogleSignInService(private val supabaseClient: SupabaseClient) {

    companion object {
        private var activityRef: WeakReference<Activity>? = null
        private val currentActivity: Activity? get() = activityRef?.get()

        fun setActivity(activity: Activity) {
            activityRef = WeakReference(activity)
        }
    }

    actual fun setActivityContext(context: Any) {
        val activity = findActivity(context as? Context)
        if (activity != null) {
            activityRef = WeakReference(activity)
        }
    }

    actual suspend fun signIn(): GoogleSignInResult {
        val activity = currentActivity
            ?: return GoogleSignInResult.Error("No activity context available")

        return try {
            val credentialManager = CredentialManager.create(activity)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(AuthConfig.googleWebClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activity, request)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val idToken = googleIdTokenCredential.idToken

            GoogleSignInResult.Success(idToken)
        } catch (e: GetCredentialCancellationException) {
            GoogleSignInResult.Cancelled
        } catch (e: NoCredentialException) {
            // Credential Manager failed — fall back to browser-based Google OAuth
            try {
                supabaseClient.auth.signInWith(Google)
                GoogleSignInResult.BrowserFlowStarted
            } catch (fallbackError: Exception) {
                GoogleSignInResult.Error(fallbackError.message ?: "Google sign-in failed")
            }
        } catch (e: Exception) {
            GoogleSignInResult.Error(e.message ?: "Google sign-in failed")
        }
    }

    private fun findActivity(context: Context?): Activity? {
        var ctx = context ?: return null
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }
}
