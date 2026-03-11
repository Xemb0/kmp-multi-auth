package io.github.xemb0.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import org.koin.mp.KoinPlatform
import platform.Foundation.NSURL

/**
 * Call this from your iOSApp.swift onOpenURL handler to handle
 * auth deep links (magic link, OAuth callbacks).
 *
 * Usage in Swift:
 * ```swift
 * .onOpenURL { url in
 *     DeepLinkHandler_iosKt.handleAuthDeepLink(url: url)
 * }
 * ```
 */
fun handleAuthDeepLink(url: NSURL) {
    val supabaseClient = KoinPlatform.getKoin().get<SupabaseClient>()
    supabaseClient.handleDeeplinks(url)
}
