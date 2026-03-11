package io.github.xemb0.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.xemb0.auth.AuthConfig
import io.github.xemb0.auth.AuthManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * "Check your email" screen shown after sending a magic link.
 * Watches for session authentication via deep link callback.
 *
 * @param email The email address the magic link was sent to
 * @param authManager The auth manager instance
 * @param supabaseClient The Supabase client instance
 * @param onAuthenticated Called when authentication succeeds via magic link
 * @param onBack Called when user taps back
 */
@Composable
fun MagicLinkOtpScreen(
    email: String,
    authManager: AuthManager,
    supabaseClient: SupabaseClient,
    onAuthenticated: () -> Unit,
    onBack: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendCooldown by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val openEmailApp = rememberOpenEmailApp()
    val strings = AuthConfig.strings

    val primary = AuthConfig.primaryColor ?: MaterialTheme.colorScheme.primary
    val accent = AuthConfig.accentColor ?: MaterialTheme.colorScheme.secondary
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    // Watch for session becoming authenticated (user clicked the magic link)
    LaunchedEffect(Unit) {
        supabaseClient.auth.sessionStatus.collect { status ->
            if (status is SessionStatus.Authenticated) {
                onAuthenticated()
            }
        }
    }

    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000L)
            resendCooldown--
        }
    }

    fun resend() {
        if (resendCooldown > 0) return
        scope.launch {
            try {
                authManager.sendMagicLink(email)
                resendCooldown = 60
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = e.message ?: strings.resendError
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp)
            .padding(top = 48.dp, bottom = 32.dp)
    ) {
        // Back button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(textPrimary.copy(alpha = 0.06f))
                .border(1.dp, textPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "\u2190", fontSize = 18.sp, color = textPrimary)
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Email icon
        Text(
            text = "\u2709\uFE0F",
            fontSize = 64.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = strings.checkYourEmail,
            style = MaterialTheme.typography.headlineMedium,
            color = textPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = strings.magicLinkSentTo,
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = strings.magicLinkInstructions,
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Open Email button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(primary, accent)),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Button(
                onClick = { openEmailApp() },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = strings.openEmailApp,
                    style = MaterialTheme.typography.titleMedium,
                    color = textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resend link
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = { resend() },
                enabled = resendCooldown == 0
            ) {
                Text(
                    text = if (resendCooldown > 0) strings.resendLinkCountdown(resendCooldown) else strings.resendLink,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (resendCooldown > 0) textSecondary else primary
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
