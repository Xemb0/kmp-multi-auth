package io.github.xemb0.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
internal actual fun rememberOpenEmailApp(): () -> Unit {
    return remember {
        {
            val url = NSURL.URLWithString("mailto:")
            if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
                UIApplication.sharedApplication.openURL(url)
            }
        }
    }
}
