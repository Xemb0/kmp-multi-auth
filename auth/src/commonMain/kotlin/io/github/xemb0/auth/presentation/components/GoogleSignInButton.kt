package io.github.xemb0.auth.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xemb0.auth.AuthConfig
import io.github.xemb0.auth.AuthResult
import io.github.xemb0.auth.AuthUser
import io.github.xemb0.auth.GoogleSignInResult
import io.github.xemb0.auth.GoogleSignInService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import io.github.xemb0.auth.presentation.SetupGoogleSignIn

/**
 * Standalone Google Sign-In button. Use this to embed Google sign-in anywhere
 * without the full AuthScreen.
 *
 * Handles the entire flow: Credential Manager → ID token → Supabase sign-in.
 * For browser-based OAuth fallback, the caller must also listen for session changes
 * via [SupabaseClient.auth.sessionStatus].
 *
 * @param supabaseClient Supabase client with Auth installed
 * @param googleSignInService Platform-specific Google sign-in service
 * @param onResult Called with the authentication result
 * @param modifier Modifier for the button
 * @param text Button label
 * @param shape Button shape
 * @param height Button height
 * @param enabled Whether the button is enabled
 */
@Composable
fun GoogleSignInButton(
    supabaseClient: SupabaseClient,
    googleSignInService: GoogleSignInService,
    onResult: (AuthResult) -> Unit,
    modifier: Modifier = Modifier,
    text: String = AuthConfig.strings.continueWithGoogle,
    shape: Shape = RoundedCornerShape(12.dp),
    height: Dp = 52.dp,
    enabled: Boolean = true,
    onLoadingChanged: (Boolean) -> Unit = {},
    cancelSignal: Int = 0,
) {
    // NOTE: [height] is treated as a *minimum* (heightIn). Using a fixed .height(...) would
    // clip the icon/label at larger system font scales. The button grows to fit its content.
    SetupGoogleSignIn()
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var currentJob by remember { mutableStateOf<Job?>(null) }
    LaunchedEffect(isLoading) { onLoadingChanged(isLoading) }
    // External cancel: parent increments cancelSignal → we abort any in-flight job
    // and reset the spinner so the user can retry.
    LaunchedEffect(cancelSignal) {
        if (cancelSignal > 0 && isLoading) {
            currentJob?.cancel()
            currentJob = null
            isLoading = false
        }
    }

    val textColor = MaterialTheme.colorScheme.onBackground
    val borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    val primary = AuthConfig.primaryColor ?: MaterialTheme.colorScheme.primary

    OutlinedButton(
        onClick = {
            if (isLoading) {
                // Second tap while loading = cancel in-flight sign-in.
                currentJob?.cancel()
                currentJob = null
                isLoading = false
                return@OutlinedButton
            }
            isLoading = true
            currentJob = scope.launch {
                try {
                    when (val result = googleSignInService.signIn()) {
                        is GoogleSignInResult.Success -> {
                            supabaseClient.auth.signInWith(IDToken) {
                                idToken = result.idToken
                                provider = Google
                            }
                            val user = supabaseClient.auth.currentUserOrNull()
                            isLoading = false
                            currentJob = null
                            if (user != null) {
                                onResult(AuthResult.Success(
                                    AuthUser(
                                        id = user.id,
                                        email = user.email,
                                        displayName = user.userMetadata?.get("full_name")?.toString()?.trim('"'),
                                        avatarUrl = user.userMetadata?.get("avatar_url")?.toString()?.trim('"'),
                                        isAnonymous = false
                                    )
                                ))
                            }
                        }
                        is GoogleSignInResult.BrowserFlowStarted -> {
                            // iOS ASWebAuthenticationSession path: signIn() suspends
                            // until the Supabase session is set, so by the time we
                            // get here the user is authenticated. The screen-level
                            // sessionStatus collector handles navigation; we just
                            // need to clear our local spinner.
                            isLoading = false
                            currentJob = null
                        }
                        is GoogleSignInResult.Cancelled -> {
                            isLoading = false
                            currentJob = null
                            onResult(AuthResult.Cancelled)
                        }
                        is GoogleSignInResult.Error -> {
                            isLoading = false
                            currentJob = null
                            onResult(AuthResult.Error(result.message))
                        }
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    isLoading = false
                    currentJob = null
                    throw e
                } catch (e: Exception) {
                    isLoading = false
                    currentJob = null
                    onResult(AuthResult.Error(e.message ?: AuthConfig.strings.signInFailed))
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = height),
        shape = shape,
        enabled = enabled,
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = primary,
                strokeWidth = 2.dp
            )
        } else {
            Image(
                painter = rememberVectorPainter(AuthIcons.Google),
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
        }
    }
}
