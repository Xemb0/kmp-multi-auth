package io.github.xemb0.auth.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberOpenEmailApp(): () -> Unit {
    val context = LocalContext.current
    return remember {
        {
            // Try several strategies in order — whichever resolves first wins. Using
            // startActivity + try/catch instead of resolveActivity so we don't need the host
            // app to declare <queries> for Android 11+ package visibility.
            val attempts: List<() -> Intent?> = listOf(
                // 1. Launch Gmail explicitly if installed.
                {
                    context.packageManager.getLaunchIntentForPackage("com.google.android.gm")
                        ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                },
                // 2. Whatever app is registered as the system default email app.
                {
                    Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                },
                // 3. Any app that handles mailto: (Gmail, Outlook, Samsung Mail, etc).
                {
                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                },
                // 4. Last resort — let the user pick from a chooser.
                {
                    Intent.createChooser(
                        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")),
                        "Open email app"
                    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                }
            )
            run {
                for (build in attempts) {
                    val intent = build() ?: continue
                    try {
                        context.startActivity(intent)
                        return@run
                    } catch (_: Exception) {
                        // Try the next strategy.
                    }
                }
                // All strategies failed — no email app available. Silent no-op.
            }
        }
    }
}
