package io.github.xemb0.auth.presentation

import androidx.compose.runtime.Composable

/**
 * Intercepts the hardware/gesture back navigation on platforms that have one (Android).
 * On iOS this is a no-op — the host navigation stack handles the swipe-back gesture.
 */
@Composable
internal expect fun PlatformBackHandler(enabled: Boolean = true, onBack: () -> Unit)
