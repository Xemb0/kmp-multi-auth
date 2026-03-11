package io.github.xemb0.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google

actual class GoogleSignInService(private val supabaseClient: SupabaseClient) {
    actual fun setActivityContext(context: Any) {
        // No-op on iOS
    }

    actual suspend fun signIn(): GoogleSignInResult {
        return try {
            supabaseClient.auth.signInWith(Google)
            GoogleSignInResult.BrowserFlowStarted
        } catch (e: Exception) {
            GoogleSignInResult.Error(e.message ?: "Google sign-in failed")
        }
    }
}
