package io.github.xemb0.auth.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    enabled: Boolean = true
) {
    SetupGoogleSignIn()
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val textColor = MaterialTheme.colorScheme.onBackground
    val borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    val primary = AuthConfig.primaryColor ?: MaterialTheme.colorScheme.primary

    OutlinedButton(
        onClick = {
            if (isLoading) return@OutlinedButton
            isLoading = true
            scope.launch {
                try {
                    when (val result = googleSignInService.signIn()) {
                        is GoogleSignInResult.Success -> {
                            supabaseClient.auth.signInWith(IDToken) {
                                idToken = result.idToken
                                provider = Google
                            }
                            val user = supabaseClient.auth.currentUserOrNull()
                            isLoading = false
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
                            // Session collector in the caller handles this
                        }
                        is GoogleSignInResult.Cancelled -> {
                            isLoading = false
                            onResult(AuthResult.Cancelled)
                        }
                        is GoogleSignInResult.Error -> {
                            isLoading = false
                            onResult(AuthResult.Error(result.message))
                        }
                    }
                } catch (e: Exception) {
                    isLoading = false
                    onResult(AuthResult.Error(e.message ?: AuthConfig.strings.signInFailed))
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = shape,
        enabled = enabled && !isLoading,
        border = BorderStroke(1.dp, borderColor)
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
