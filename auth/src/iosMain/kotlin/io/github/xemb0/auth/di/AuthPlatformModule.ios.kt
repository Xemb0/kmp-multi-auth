package io.github.xemb0.auth.di

import io.github.xemb0.auth.GoogleSignInService
import org.koin.core.module.Module
import org.koin.dsl.module

actual val authPlatformModule: Module = module {
    single { GoogleSignInService(get()) }
}
