package io.github.xemb0.auth.presentation

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS navigation stacks handle the swipe-back gesture natively; nothing to intercept here.
}
