package io.github.xemb0.auth

import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin

internal actual fun ComposeAuth.Config.configureSocialLogins() {
    appleNativeLogin()
}
