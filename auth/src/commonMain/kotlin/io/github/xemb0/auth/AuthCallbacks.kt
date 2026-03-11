package io.github.xemb0.auth

/**
 * Callback interface for auth events. The consuming app implements this
 * and provides it via Koin DI (or passes directly to screens).
 *
 * All methods have default no-op implementations so you only override what you need.
 */
interface AuthCallbacks {
    /** Called when a user successfully authenticates. */
    suspend fun onLoginSuccess(userId: String, method: String) {}

    /** Called when authentication fails. */
    suspend fun onLoginError(method: String, error: String) {}

    /** Called when the user signs out. */
    suspend fun onSignOut() {}
}
