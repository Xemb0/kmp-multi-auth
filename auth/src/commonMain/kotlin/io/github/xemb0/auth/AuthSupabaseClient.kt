package io.github.xemb0.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.createSupabaseClient
import kotlin.time.Duration.Companion.seconds

/**
 * Creates a SupabaseClient configured for authentication.
 *
 * Apps can either:
 * 1. Use this to create an auth-only client
 * 2. Create their own SupabaseClient with additional plugins (Postgrest, Storage, etc.)
 *    and pass it to the auth module via Koin — just make sure Auth + ComposeAuth are installed.
 *
 * @param additionalConfig Optional lambda to install additional Supabase plugins (Postgrest, Storage, etc.)
 */
fun createAuthSupabaseClient(
    additionalConfig: (io.github.jan.supabase.SupabaseClientBuilder.() -> Unit)? = null
): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = AuthConfig.supabaseUrl,
        supabaseKey = AuthConfig.supabaseAnonKey
    ) {
        requestTimeout = 30.seconds

        install(Auth) {
            scheme = AuthConfig.deepLinkScheme
            host = AuthConfig.deepLinkHost
            alwaysAutoRefresh = true
        }
        install(ComposeAuth) {
            configureSocialLogins()
        }

        additionalConfig?.invoke(this)
    }
}
