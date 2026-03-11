package io.github.xemb0.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import io.github.xemb0.auth.GoogleSignInService
import org.koin.compose.koinInject

@Composable
internal actual fun SetupGoogleSignIn() {
    val context = LocalContext.current
    val googleSignInService = koinInject<GoogleSignInService>()
    LaunchedEffect(context) {
        googleSignInService.setActivityContext(context)
    }
}
