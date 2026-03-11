# KMP Multi Auth

A **Kotlin Multiplatform** authentication library for **Android & iOS** — built on [Supabase Auth](https://supabase.com/docs/guides/auth).

One dependency. Four auth methods. Zero boilerplate.

## Features

- **Google Sign-In** — Native Credential Manager (Android) + OAuth (iOS)
- **Apple Sign-In** — Native Sign in with Apple via Supabase ComposeAuth
- **Magic Link (Email OTP)** — Passwordless email authentication via Supabase
- **Anonymous / Guest** — Let users try your app without signing up
- **Kotlin Multiplatform** — Single codebase for Android + iOS
- **Compose Multiplatform UI** — Ready-made screens and individual buttons
- **Fully Customizable** — Colors, strings, branding, auth methods, i18n
- **3-Level API** — Use full screens, individual buttons, or headless auth
- **Dark Mode** — Automatic light/dark theme support via MaterialTheme
- **Reactive State** — `Flow<AuthState>` for observing session changes

## Installation

[![Maven Central](https://img.shields.io/maven-central/v/io.github.xemb0/auth)](https://central.sonatype.com/artifact/io.github.xemb0/auth)

Add the dependency in your module's `build.gradle.kts`:

```kotlin
// KMP (commonMain)
implementation("io.github.xemb0:auth:1.0.0")

// Android-only
implementation("io.github.xemb0:auth-android:1.0.0")
```

That's it — resolves from Maven Central, no extra repositories needed.

## Quick Start

### 1. Initialize

```kotlin
AuthConfig.initialize(
    supabaseUrl = "https://your-project.supabase.co",
    supabaseAnonKey = "your-anon-key",
    googleWebClientId = "your-google-client-id",
    deepLinkScheme = "com.your.app",
    appName = "MyApp",
    appTagline = "Welcome back!",
    enabledMethods = setOf(
        AuthMethod.GOOGLE,
        AuthMethod.APPLE,
        AuthMethod.EMAIL,       // Magic Link
        AuthMethod.ANONYMOUS    // Optional guest mode
    )
)
```

### 2. Show the Auth Screen

```kotlin
AuthScreen(
    supabaseClient = supabaseClient,
    googleSignInService = googleSignInService,
    authManager = authManager,
    onAuthenticated = { /* navigate to home */ },
    onEmailSignIn = { /* navigate to magic link flow */ }
)
```

That's it. You get a fully styled login screen with all enabled providers.

## 3-Level API

### Level 1: Full Screens

Drop-in screens that handle the entire auth flow.

```kotlin
// Auth gate — auto-routes based on session state
AuthGate(
    authManager = authManager,
    authContent = { AuthScreen(...) },
    authenticatedContent = { HomeScreen() }
)
```

**Screens included:**
| Screen | Description |
|---|---|
| `AuthScreen` | Full login screen with all configured providers |
| `AuthGate` | Session-aware gate (loading → login or content) |
| `MagicLinkEmailScreen` | Email input for magic link / OTP |
| `MagicLinkOtpScreen` | "Check your email" confirmation screen |

### Level 2: Individual Buttons

Use standalone buttons anywhere in your UI.

```kotlin
GoogleSignInButton(
    supabaseClient = supabaseClient,
    googleSignInService = googleSignInService,
    onResult = { result ->
        when (result) {
            is AuthResult.Success -> { /* logged in */ }
            is AuthResult.Cancelled -> { /* user cancelled */ }
            is AuthResult.Error -> { /* show error */ }
        }
    }
)

AppleSignInButton(
    supabaseClient = supabaseClient,
    onResult = { /* handle result */ }
)

EmailSignInButton(
    onClick = { /* navigate to magic link flow */ }
)

AnonymousSignInButton(
    authManager = authManager,
    onResult = { /* handle result */ }
)
```

All buttons are individually customizable (text, shape, height, colors).

### Level 3: Headless

Use `AuthManager` directly — no UI, full control.

```kotlin
val authManager: AuthManager = SupabaseAuthManager(supabaseClient)

// Anonymous sign-in
val userId = authManager.signInAnonymously()

// Magic link
authManager.sendMagicLink("user@example.com")

// Check session
val isLoggedIn = authManager.isAuthenticated()
val user: AuthUser? = authManager.getCurrentUser()
val token: String? = authManager.getAccessToken()

// Observe auth state reactively
authManager.observeAuthState().collect { state ->
    when (state) {
        is AuthState.Loading -> { /* restoring session */ }
        is AuthState.Authenticated -> { /* user.id = state.userId */ }
        is AuthState.Unauthenticated -> { /* show login */ }
    }
}

// Sign out
authManager.signOut()
```

## Customization

### Branding

```kotlin
AuthConfig.initialize(
    // ...
    appName = "MyApp",
    appTagline = "Your awesome tagline",
    appLogo = { Image(painterResource(R.drawable.logo), "Logo") },  // or null for text
    appNameColor = Color(0xFF6C63FF),
    primaryColor = Color(0xFF6C63FF),    // null = use MaterialTheme
    accentColor = Color(0xFFFF6584),
)
```

### Enable/Disable Auth Methods

```kotlin
// Only Google + Email (Magic Link)
enabledMethods = setOf(AuthMethod.GOOGLE, AuthMethod.EMAIL)

// Only Apple
enabledMethods = setOf(AuthMethod.APPLE)

// All four
enabledMethods = setOf(AuthMethod.GOOGLE, AuthMethod.APPLE, AuthMethod.EMAIL, AuthMethod.ANONYMOUS)
```

### Custom Strings (i18n)

```kotlin
AuthConfig.initialize(
    // ...
    strings = object : AuthStrings() {
        override val welcome = "Bienvenido"
        override val continueWithGoogle = "Continuar con Google"
        override val signInWithApple = "Iniciar sesion con Apple"
        override val continueWithEmail = "Continuar con Email"
        override val continueAsGuest = "Continuar como invitado"
        override val orDivider = "o"
        override val termsOfService = "Al continuar, aceptas nuestros Terminos de Servicio"
    }
)
```

### Content Slots

```kotlin
AuthScreen(
    // ...
    headerContent = {
        Text("Special offer: 50% off!", style = MaterialTheme.typography.bodyLarge)
    },
    footerContent = {
        TextButton(onClick = { /* privacy policy */ }) {
            Text("Privacy Policy")
        }
    }
)
```

### Auth Callbacks (Analytics)

```kotlin
val callbacks = object : AuthCallbacks {
    override suspend fun onLoginSuccess(userId: String, method: String) {
        analytics.log("login_success", mapOf("method" to method))
    }
    override suspend fun onLoginError(method: String, error: String) {
        analytics.log("login_error", mapOf("method" to method, "error" to error))
    }
    override suspend fun onSignOut() {
        analytics.log("sign_out")
    }
}
```

## Koin Integration

The library ships with Koin modules for dependency injection:

```kotlin
// In your app's Koin setup
startKoin {
    modules(
        authModule,           // provides AuthManager
        authPlatformModule,   // provides GoogleSignInService (platform-specific)
        // your app modules...
    )
}
```

## Deep Link Setup

### Android

Add to your `AndroidManifest.xml`:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="com.your.app" android:host="login-callback" />
</intent-filter>
```

Handle in your Activity:

```kotlin
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleAuthDeepLink(intent)
}
```

### iOS

Add URL scheme in `Info.plist` and handle in `AppDelegate`:

```swift
func application(_ app: UIApplication, open url: URL, options: ...) -> Bool {
    handleAuthDeepLink(url: url)
    return true
}
```

## Requirements

| | Version |
|---|---|
| Kotlin | 2.3+ |
| Compose Multiplatform | 1.10+ |
| Supabase BOM | 3.3+ |
| Android minSdk | 26 |
| iOS | arm64, simulatorArm64 |

## License

```
MIT License

Copyright (c) 2025 Xemb0

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
