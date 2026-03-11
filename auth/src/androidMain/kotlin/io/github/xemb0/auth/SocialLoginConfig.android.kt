package io.github.xemb0.auth

import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin

internal actual fun ComposeAuth.Config.configureSocialLogins() {
    if (AuthConfig.googleWebClientId.isNotBlank()) {
        googleNativeLogin(serverClientId = AuthConfig.googleWebClientId)
    }
}
