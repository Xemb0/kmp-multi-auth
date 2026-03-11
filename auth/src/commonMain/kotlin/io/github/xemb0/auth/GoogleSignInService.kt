package io.github.xemb0.auth

expect class GoogleSignInService {
    fun setActivityContext(context: Any)
    suspend fun signIn(): GoogleSignInResult
}

sealed interface GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult
    data object BrowserFlowStarted : GoogleSignInResult
    data object Cancelled : GoogleSignInResult
    data class Error(val message: String) : GoogleSignInResult
}
