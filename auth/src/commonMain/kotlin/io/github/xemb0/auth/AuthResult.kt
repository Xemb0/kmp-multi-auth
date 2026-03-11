package io.github.xemb0.auth

/**
 * Unified result type for all auth operations.
 * Used by both headless AuthManager methods and composable buttons.
 */
sealed interface AuthResult {
    data class Success(val user: AuthUser) : AuthResult
    data object Cancelled : AuthResult
    data class Error(val message: String) : AuthResult
}
