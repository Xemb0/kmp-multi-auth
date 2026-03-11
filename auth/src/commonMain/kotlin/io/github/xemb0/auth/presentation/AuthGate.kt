package io.github.xemb0.auth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.xemb0.auth.AuthManager
import io.github.xemb0.auth.AuthState

/**
 * Session gate that shows loading while checking auth state, then routes to
 * either authenticated content or the auth flow.
 *
 * Usage:
 * ```
 * AuthGate(
 *     authManager = authManager,
 *     authContent = { AuthScreen(...) },
 *     authenticatedContent = { HomeScreen() }
 * )
 * ```
 *
 * @param authManager The auth manager instance
 * @param authContent Composable shown when user is NOT authenticated (show login screen)
 * @param authenticatedContent Composable shown when user IS authenticated
 * @param loadingContent Optional composable shown while session is being restored
 */
@Composable
fun AuthGate(
    authManager: AuthManager,
    authContent: @Composable () -> Unit,
    authenticatedContent: @Composable () -> Unit,
    loadingContent: (@Composable () -> Unit)? = null
) {
    val authState by authManager.observeAuthState()
        .collectAsState(initial = AuthState.Loading)

    when (authState) {
        is AuthState.Loading -> {
            if (loadingContent != null) {
                loadingContent()
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        is AuthState.Authenticated -> {
            authenticatedContent()
        }
        is AuthState.Unauthenticated -> {
            authContent()
        }
    }
}
