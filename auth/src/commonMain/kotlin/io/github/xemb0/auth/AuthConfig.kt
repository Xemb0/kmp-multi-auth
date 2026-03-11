package io.github.xemb0.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Available authentication methods that can be enabled/disabled.
 */
enum class AuthMethod {
    GOOGLE,
    APPLE,
    EMAIL,
    ANONYMOUS
}

/**
 * Customizable strings for all auth screens. Override any property to change text.
 * Supports localization by providing a translated implementation.
 */
open class AuthStrings {
    open val welcome: String = "Welcome"
    open val continueWithGoogle: String = "Continue with Google"
    open val signInWithApple: String = "Sign in with Apple"
    open val continueWithEmail: String = "Continue with Email"
    open val orDivider: String = "or"
    open val cancel: String = "Cancel"
    open val termsOfService: String = "By continuing, you agree to our Terms of Service"
    open val signInWithEmail: String = "Sign in with Email"
    open val emailSubtitle: String = "We'll send a verification code to your email"
    open val emailLabel: String = "Email address"
    open val sendCode: String = "Send Code"
    open val sendCodeError: String = "Failed to send code. Try again."
    open val checkYourEmail: String = "Check your email"
    open val magicLinkSentTo: String = "We sent a magic link to"
    open val magicLinkInstructions: String = "Tap the link in the email to sign in.\nYou'll be redirected back here automatically."
    open val openEmailApp: String = "Open Email App"
    open val resendLink: String = "Resend link"
    open fun resendLinkCountdown(seconds: Int): String = "Resend link in ${seconds}s"
    open val resendError: String = "Failed to resend link."
    open val networkError: String = "Network error. Check your connection and try again."
    open val signInFailed: String = "Sign-in failed"
    open fun appleUnavailable(message: String?): String = "Apple Sign-In unavailable: $message"
    open val continueAsGuest: String = "Continue as Guest"
    open val guestSignInFailed: String = "Guest sign-in failed"
    open val skipForNow: String = "Skip for now"
}

/**
 * Configuration for the auth module. Must be initialized before using auth screens.
 *
 * Usage:
 * ```
 * AuthConfig.initialize(
 *     supabaseUrl = "https://your-project.supabase.co",
 *     supabaseAnonKey = "your-anon-key",
 *     googleWebClientId = "your-google-client-id",
 *     deepLinkScheme = "com.your.app",
 *     appName = "YourApp"
 * )
 * ```
 */
object AuthConfig {
    var supabaseUrl: String = ""
        private set
    var supabaseAnonKey: String = ""
        private set
    var googleWebClientId: String = ""
        private set
    var deepLinkScheme: String = ""
        private set
    var deepLinkHost: String = "login-callback"
        private set

    // Branding
    var appName: String = ""
        private set
    var appTagline: String = "Sign in to continue"
        private set

    // Logo — composable slot, if null shows appName as text
    var appLogo: (@Composable () -> Unit)? = null
        private set

    // Auth method toggles
    var enabledMethods: Set<AuthMethod> = setOf(AuthMethod.GOOGLE, AuthMethod.APPLE, AuthMethod.EMAIL)
        private set

    // Colors — null means derive from MaterialTheme
    var primaryColor: Color? = null
        private set
    var accentColor: Color? = null
        private set
    var appNameColor: Color? = null
        private set

    // Strings — override for i18n or custom wording
    var strings: AuthStrings = AuthStrings()
        private set

    // Terms / Privacy URLs — null hides the links
    var termsOfServiceUrl: String? = null
        private set
    var privacyPolicyUrl: String? = null
        private set

    // Email validation — override for custom rules
    var emailValidator: (String) -> Boolean = { email ->
        email.matches(Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
    }
        private set

    private var initialized = false

    fun initialize(
        supabaseUrl: String,
        supabaseAnonKey: String,
        googleWebClientId: String,
        deepLinkScheme: String,
        deepLinkHost: String = "login-callback",
        appName: String,
        appTagline: String = "Sign in to continue",
        appLogo: (@Composable () -> Unit)? = null,
        enabledMethods: Set<AuthMethod> = setOf(AuthMethod.GOOGLE, AuthMethod.APPLE, AuthMethod.EMAIL),
        primaryColor: Color? = null,
        accentColor: Color? = null,
        appNameColor: Color? = null,
        strings: AuthStrings = AuthStrings(),
        termsOfServiceUrl: String? = null,
        privacyPolicyUrl: String? = null,
        emailValidator: ((String) -> Boolean)? = null
    ) {
        this.supabaseUrl = supabaseUrl
        this.supabaseAnonKey = supabaseAnonKey
        this.googleWebClientId = googleWebClientId
        this.deepLinkScheme = deepLinkScheme
        this.deepLinkHost = deepLinkHost
        this.appName = appName
        this.appTagline = appTagline
        this.appLogo = appLogo
        this.enabledMethods = enabledMethods
        this.primaryColor = primaryColor
        this.accentColor = accentColor
        this.appNameColor = appNameColor
        this.strings = strings
        this.termsOfServiceUrl = termsOfServiceUrl
        this.privacyPolicyUrl = privacyPolicyUrl
        if (emailValidator != null) this.emailValidator = emailValidator
        this.initialized = true
    }

    fun isInitialized(): Boolean = initialized
}
