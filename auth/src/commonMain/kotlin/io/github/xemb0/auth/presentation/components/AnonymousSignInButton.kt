package io.github.xemb0.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import io.github.xemb0.auth.AuthManager
import io.github.xemb0.auth.AuthResult
import io.github.xemb0.auth.AuthUser
import kotlinx.coroutines.launch

/**
 * Standalone "Continue as Guest" / anonymous sign-in button.
 *
 * Creates an anonymous Supabase user so the app can function without
 * requiring a real sign-up. The anonymous account can be upgraded later
 * by linking an email or social provider.
 *
 * @param authManager The auth manager instance
 * @param onResult Called with the authentication result
 * @param modifier Modifier for the button
 * @param text Button label
 * @param shape Button shape
 * @param height Button height
 * @param enabled Whether the button is enabled
 */
@Composable
fun AnonymousSignInButton(
    authManager: AuthManager,
    onResult: (AuthResult) -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Continue as Guest",
    shape: Shape = RoundedCornerShape(12.dp),
    height: Dp = 48.dp,
    enabled: Boolean = true
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    TextButton(
        onClick = {
            if (isLoading) return@TextButton
            isLoading = true
            scope.launch {
                try {
                    val userId = authManager.signInAnonymously()
                    isLoading = false
                    onResult(AuthResult.Success(
                        AuthUser(
                            id = userId,
                            email = null,
                            displayName = null,
                            avatarUrl = null,
                            isAnonymous = true
                        )
                    ))
                } catch (e: Exception) {
                    isLoading = false
                    onResult(AuthResult.Error(e.message ?: "Anonymous sign-in failed"))
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = shape,
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
        }
    }
}
