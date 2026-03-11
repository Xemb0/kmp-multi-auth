package io.github.xemb0.auth.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin module for auth. Include this alongside [authModule].
 */
expect val authPlatformModule: Module
