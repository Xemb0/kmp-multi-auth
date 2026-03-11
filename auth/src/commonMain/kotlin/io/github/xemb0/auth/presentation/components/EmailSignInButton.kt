package io.github.xemb0.auth.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.xemb0.auth.AuthConfig
import org.jetbrains.compose.resources.painterResource
import io.github.xemb0.auth.resources.Res
import io.github.xemb0.auth.resources.ic_email

/**
 * Standalone Email Sign-In button. This is a styled button that triggers
 * navigation to the magic link email flow.
 *
 * Does not perform any auth logic — just a UI component that calls [onClick].
 * Pair with [io.github.xemb0.auth.presentation.MagicLinkEmailScreen] and
 * [io.github.xemb0.auth.presentation.MagicLinkOtpScreen] for the full flow.
 *
 * @param onClick Called when the button is tapped (navigate to email entry)
 * @param modifier Modifier for the button
 * @param text Button label
 * @param shape Button shape
 * @param height Button height
 * @param enabled Whether the button is enabled
 */
@Composable
fun EmailSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = AuthConfig.strings.continueWithEmail,
    shape: Shape = RoundedCornerShape(12.dp),
    height: Dp = 52.dp,
    enabled: Boolean = true
) {
    val primary = AuthConfig.primaryColor ?: MaterialTheme.colorScheme.primary

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = shape,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = primary,
            contentColor = Color.White
        )
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_email),
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
