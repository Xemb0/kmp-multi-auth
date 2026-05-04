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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xemb0.auth.AuthConfig
import io.github.xemb0.auth.AuthResult
import io.github.xemb0.auth.AuthUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithApple
import androidx.compose.ui.graphics.vector.rememberVectorPainter

/**
 * Standalone Apple Sign-In button. Use this to embed Apple sign-in anywhere
 * without the full AuthScreen.
 *
 * Uses Supabase ComposeAuth's native Apple sign-in flow.
 *
 * @param supabaseClient Supabase client with Auth + ComposeAuth installed
 * @param onResult Called with the authentication result
 * @param modifier Modifier for the button
 * @param text Button label
 * @param shape Button shape
 * @param height Button height
 * @param enabled Whether the button is enabled
 */
@Composable
fun AppleSignInButton(
    supabaseClient: SupabaseClient,
    onResult: (AuthResult) -> Unit,
    modifier: Modifier = Modifier,
    text: String = AuthConfig.strings.signInWithApple,
    shape: Shape = RoundedCornerShape(12.dp),
    height: Dp = 52.dp,
    enabled: Boolean = true,
    onLoadingChanged: (Boolean) -> Unit = {},
    cancelSignal: Int = 0,
) {
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(isLoading) { onLoadingChanged(isLoading) }
    // External cancel — parent bumps cancelSignal to reset our spinner. Apple's
    // native sheet has its own Close button, so there's no job to cancel; we
    // just reset local UI state.
    LaunchedEffect(cancelSignal) {
        if (cancelSignal > 0 && isLoading) {
            isLoading = false
        }
    }

    val textColor = MaterialTheme.colorScheme.onBackground
    val borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
    val primary = AuthConfig.primaryColor ?: MaterialTheme.colorScheme.primary

    val appleSignInState = supabaseClient.composeAuth.rememberSignInWithApple(
        onResult = { result ->
            isLoading = false
            when (result) {
                is NativeSignInResult.Success -> {
                    val user = supabaseClient.auth.currentUserOrNull()
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
                is NativeSignInResult.ClosedByUser -> {
                    onResult(AuthResult.Cancelled)
                }
                is NativeSignInResult.NetworkError -> {
                    onResult(AuthResult.Error(AuthConfig.strings.networkError))
                }
                is NativeSignInResult.Error -> {
                    onResult(AuthResult.Error(result.message))
                }
            }
        }
    )

    OutlinedButton(
        onClick = {
            if (isLoading) {
                // Tapping the button again while loading resets state so the
                // user can retry without relaunching the app.
                isLoading = false
                return@OutlinedButton
            }
            isLoading = true
            try {
                appleSignInState.startFlow()
            } catch (e: Exception) {
                isLoading = false
                onResult(AuthResult.Error(AuthConfig.strings.appleUnavailable(e.message)))
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
                painter = rememberVectorPainter(AuthIcons.Apple),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(textColor)
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
