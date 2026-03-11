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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.xemb0.auth.AuthConfig
import io.github.xemb0.auth.AuthManager
import kotlinx.coroutines.launch

/**
 * Email input screen for magic link authentication.
 *
 * @param authManager The auth manager instance
 * @param onCodeSent Called after magic link is sent, with the email address
 * @param onBack Called when user taps back
 */
@Composable
fun MagicLinkEmailScreen(
    authManager: AuthManager,
    onCodeSent: (email: String) -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val strings = AuthConfig.strings

    val primary = AuthConfig.primaryColor ?: MaterialTheme.colorScheme.primary
    val accent = AuthConfig.accentColor ?: MaterialTheme.colorScheme.secondary
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val isValidEmail = AuthConfig.emailValidator(email.trim())

    fun submit() {
        if (!isValidEmail || isLoading) return
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                authManager.sendMagicLink(email.trim())
                isLoading = false
                onCodeSent(email.trim())
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.message ?: strings.sendCodeError
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

        Text(
            text = strings.signInWithEmail,
            style = MaterialTheme.typography.headlineMedium,
            color = textPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = strings.emailSubtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(strings.emailLabel) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                focusedBorderColor = primary,
                unfocusedBorderColor = textSecondary.copy(alpha = 0.5f),
                focusedLabelColor = primary,
                unfocusedLabelColor = textSecondary,
                cursorColor = primary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            if (isValidEmail && !isLoading) primary else primary.copy(alpha = 0.4f),
                            if (isValidEmail && !isLoading) accent else accent.copy(alpha = 0.4f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Button(
                onClick = { submit() },
                enabled = isValidEmail && !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = textPrimary,
                        modifier = Modifier.height(24.dp)
                    )
                } else {
                    Text(
                        text = strings.sendCode,
                        style = MaterialTheme.typography.titleMedium,
                        color = textPrimary
                    )
                }
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
