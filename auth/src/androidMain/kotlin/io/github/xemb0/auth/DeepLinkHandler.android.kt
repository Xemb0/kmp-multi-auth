package io.github.xemb0.auth

import android.content.Intent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import org.koin.mp.KoinPlatform

/**
 * Call this from your MainActivity.onCreate() and onNewIntent() to handle
 * auth deep links (magic link, OAuth callbacks).
 */
fun handleAuthDeepLink(intent: Intent) {
    val data = intent.data ?: return
    try {
        val supabaseClient = KoinPlatform.getKoin().get<SupabaseClient>()
        supabaseClient.handleDeeplinks(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
