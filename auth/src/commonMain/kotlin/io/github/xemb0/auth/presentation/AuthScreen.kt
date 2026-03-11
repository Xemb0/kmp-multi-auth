package io.github.xemb0.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.xemb0.auth.AuthCallbacks
import io.github.xemb0.auth.AuthConfig
import io.github.xemb0.auth.AuthManager
import io.github.xemb0.auth.AuthMethod
import io.github.xemb0.auth.AuthResult
import io.github.xemb0.auth.GoogleSignInService
import io.github.xemb0.auth.presentation.components.AnonymousSignInButton
import io.github.xemb0.auth.presentation.components.AppleSignInButton
import io.github.xemb0.auth.presentation.components.EmailSignInButton
import io.github.xemb0.auth.presentation.components.GoogleSignInButton
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch

/**
 * Full authentication screen with configurable sign-in options.
 *
 * This is the **Level 1 (Full Screen)** API. For individual buttons that you can
 * embed anywhere, use the **Level 2 (Components)** API:
 * - [GoogleSignInButton]
 * - [AppleSignInButton]
 * - [EmailSignInButton]
 * - [AnonymousSignInButton]
 *
 * For headless auth (no UI), use the **Level 3 (Headless)** API:
 * - [AuthManager]
 *
 * Which buttons appear is controlled by [AuthConfig.enabledMethods].
 * Branding is controlled by [AuthConfig.appLogo] or [AuthConfig.appName].
 * All strings are customizable via [AuthConfig.strings].
 *
 * @param supabaseClient The Supabase client instance (must have Auth + ComposeAuth installed)
 * @param googleSignInService The Google sign-in service
 * @param authManager The auth manager instance (needed for anonymous sign-in)
 * @param onAuthenticated Called when authentication succeeds
 * @param onEmailSignIn Called when user taps "Continue with Email"
 * @param callbacks Optional auth callbacks for analytics/events
 * @param headerContent Optional composable slot rendered above the welcome text
 * @param footerContent Optional composable slot rendered below the terms text
 */
@Composable
fun AuthScreen(
    supabaseClient: SupabaseClient,
    googleSignInService: GoogleSignInService,
    authManager: AuthManager,
    onAuthenticated: () -> Unit,
    onEmailSignIn: () -> Unit = {},
    callbacks: AuthCallbacks? = null,
    headerContent: (@Composable () -> Unit)? = null,
    footerContent: (@Composable () -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val strings = AuthConfig.strings

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val primary = AuthConfig.primaryColor ?: MaterialTheme.colorScheme.primary
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val showGoogle = AuthMethod.GOOGLE in AuthConfig.enabledMethods
    val showApple = AuthMethod.APPLE in AuthConfig.enabledMethods
    val showEmail = AuthMethod.EMAIL in AuthConfig.enabledMethods
    val showAnonymous = AuthMethod.ANONYMOUS in AuthConfig.enabledMethods
    val hasSocial = showGoogle || showApple
    val hasEmail = showEmail
    val needsDivider = hasSocial && hasEmail

    SetupGoogleSignIn()

    // Watch for session changes (handles browser OAuth deep link callback)
    LaunchedEffect(Unit) {
        supabaseClient.auth.sessionStatus.collect { status ->
            if (status is SessionStatus.Authenticated && status.isNew) {
                isLoading = false
                val userId = status.session.user?.id ?: ""
                scope.launch { callbacks?.onLoginSuccess(userId, "social") }
                onAuthenticated()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // App branding: logo composable or text fallback
        val logo = AuthConfig.appLogo
        if (logo != null) {
            logo()
        } else if (AuthConfig.appName.isNotBlank()) {
            Text(
                text = AuthConfig.appName,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 48.sp
                ),
                color = AuthConfig.appNameColor ?: primary
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Optional header content slot
        headerContent?.invoke()

        Text(
            text = strings.welcome,
            style = MaterialTheme.typography.headlineLarge,
            color = textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = AuthConfig.appTagline,
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Google button
        if (showGoogle) {
            GoogleSignInButton(
                supabaseClient = supabaseClient,
                googleSignInService = googleSignInService,
                onResult = { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            scope.launch { callbacks?.onLoginSuccess(result.user.id, "google") }
                            onAuthenticated()
                        }
                        is AuthResult.Cancelled -> {}
                        is AuthResult.Error -> {
                            errorMessage = result.message
                            scope.launch { callbacks?.onLoginError("google", result.message) }
                        }
                    }
                },
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Apple button
        if (showApple) {
            AppleSignInButton(
                supabaseClient = supabaseClient,
                onResult = { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            scope.launch { callbacks?.onLoginSuccess(result.user.id, "apple") }
                            onAuthenticated()
                        }
                        is AuthResult.Cancelled -> {}
                        is AuthResult.Error -> {
                            errorMessage = result.message
                            scope.launch { callbacks?.onLoginError("apple", result.message) }
                        }
                    }
                },
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // "or" divider — only between social and email
        if (needsDivider) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = textSecondary.copy(alpha = 0.3f)
                )
                Text(
                    text = strings.orDivider,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondary
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = textSecondary.copy(alpha = 0.3f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Email button
        if (showEmail) {
            EmailSignInButton(
                onClick = {
                    errorMessage = null
                    onEmailSignIn()
                },
                enabled = !isLoading
            )
        }

        // Anonymous / Guest button
        if (showAnonymous) {
            Spacer(modifier = Modifier.height(16.dp))
            AnonymousSignInButton(
                authManager = authManager,
                onResult = { result ->
                    when (result) {
                        is AuthResult.Success -> {
                            scope.launch { callbacks?.onLoginSuccess(result.user.id, "anonymous") }
                            onAuthenticated()
                        }
                        is AuthResult.Cancelled -> {}
                        is AuthResult.Error -> {
                            errorMessage = result.message
                            scope.launch { callbacks?.onLoginError("anonymous", result.message) }
                        }
                    }
                },
                text = strings.continueAsGuest,
                enabled = !isLoading
            )
        }

        // Cancel button when loading
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { isLoading = false }
            ) {
                Text(
                    text = strings.cancel,
                    style = MaterialTheme.typography.titleMedium,
                    color = textSecondary
                )
            }
        }

        // Error message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Terms of service
        Text(
            text = strings.termsOfService,
            style = MaterialTheme.typography.labelMedium,
            color = textSecondary.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        // Optional footer content slot
        footerContent?.invoke()

        Spacer(modifier = Modifier.height(32.dp))
    }
}
