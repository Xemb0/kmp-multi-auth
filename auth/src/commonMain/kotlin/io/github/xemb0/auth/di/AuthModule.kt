package io.github.xemb0.auth.di

import io.github.xemb0.auth.AuthCallbacks
import io.github.xemb0.auth.AuthManager
import io.github.xemb0.auth.SupabaseAuthManager
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the auth module.
 *
 * Provides:
 * - [AuthManager] (bound to [SupabaseAuthManager])
 *
 * Requires the consuming app to provide via Koin:
 * - [io.github.jan.supabase.SupabaseClient] (with Auth + ComposeAuth plugins installed)
 * - [io.github.xemb0.auth.GoogleSignInService] (via authPlatformModule)
 * - [io.github.xemb0.auth.AuthCallbacks] (optional — app's callback implementation)
 *
 * Usage:
 * ```
 * startKoin {
 *     modules(
 *         authModule,
 *         authPlatformModule,
 *         module {
 *             single<AuthCallbacks> { MyAuthCallbacks() }  // optional
 *             single { mySupabaseClient }
 *         }
 *     )
 * }
 * ```
 */
val authModule = module {
    single<AuthManager> {
        SupabaseAuthManager(
            supabaseClient = get(),
            callbacks = getOrNull()
        )
    }
}
