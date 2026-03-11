package io.github.xemb0.auth

/**
 * Reactive auth state that consuming apps can observe.
 */
sealed interface AuthState {
    data object Loading : AuthState
    data class Authenticated(val userId: String) : AuthState
    data object Unauthenticated : AuthState
}
