package io.github.xemb0.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout

interface AuthManager {
    suspend fun ensureAuthenticated(): String
    suspend fun signInAnonymously(): String
    suspend fun sendMagicLink(email: String)
    suspend fun verifyOtp(email: String, token: String)
    fun isAuthenticated(): Boolean
    fun getCurrentUserId(): String?
    fun getCurrentUser(): AuthUser?
    fun getAccessToken(): String?
    fun observeAuthState(): Flow<AuthState>
    suspend fun signOut()
}

class SupabaseAuthManager(
    private val supabaseClient: SupabaseClient,
    private val callbacks: AuthCallbacks? = null
) : AuthManager {

    override suspend fun ensureAuthenticated(): String {
        withTimeout(10_000L) {
            supabaseClient.auth.awaitInitialization()
        }
        return supabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Not authenticated")
    }

    override suspend fun signInAnonymously(): String {
        supabaseClient.auth.signInAnonymously()
        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Failed to authenticate anonymously")
        callbacks?.onLoginSuccess(userId, "anonymous")
        return userId
    }

    override suspend fun sendMagicLink(email: String) {
        supabaseClient.auth.signInWith(OTP) {
            this.email = email
        }
    }

    override suspend fun verifyOtp(email: String, token: String) {
        supabaseClient.auth.verifyEmailOtp(
            type = OtpType.Email.MAGIC_LINK,
            email = email,
            token = token
        )
    }

    override fun isAuthenticated(): Boolean {
        return supabaseClient.auth.currentUserOrNull() != null
    }

    override fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

    override fun getCurrentUser(): AuthUser? {
        val user = supabaseClient.auth.currentUserOrNull() ?: return null
        return AuthUser(
            id = user.id,
            email = user.email,
            displayName = user.userMetadata?.get("full_name")?.toString()?.trim('"'),
            avatarUrl = user.userMetadata?.get("avatar_url")?.toString()?.trim('"'),
            isAnonymous = user.email == null && user.phone == null
        )
    }

    override fun getAccessToken(): String? {
        return supabaseClient.auth.currentAccessTokenOrNull()
    }

    override fun observeAuthState(): Flow<AuthState> {
        return supabaseClient.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    AuthState.Authenticated(status.session.user?.id ?: "")
                }
                is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
                is SessionStatus.Initializing -> AuthState.Loading
                else -> AuthState.Unauthenticated
            }
        }
    }

    override suspend fun signOut() {
        try {
            supabaseClient.auth.signOut(SignOutScope.GLOBAL)
        } catch (_: Exception) {
            // Server call failed — still clear local session
        }
        supabaseClient.auth.signOut(SignOutScope.LOCAL)
        callbacks?.onSignOut()
    }
}
