package io.github.xemb0.auth

/**
 * Module-owned user representation. Does not leak Supabase types.
 */
data class AuthUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val avatarUrl: String?,
    val isAnonymous: Boolean
)
