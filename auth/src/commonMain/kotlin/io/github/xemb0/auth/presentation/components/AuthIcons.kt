package io.github.xemb0.auth.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal object AuthIcons {

    val Google: ImageVector by lazy {
        ImageVector.Builder(
            name = "GoogleIcon",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f
        ).apply {
            path(fill = SolidColor(Color(0xFFFBBB00))) {
                moveTo(113.47f, 309.41f)
                lineTo(95.65f, 375.94f)
                lineToRelative(-65.14f, 1.38f)
                curveTo(11.04f, 341.21f, 0f, 299.9f, 0f, 256f)
                curveToRelative(0f, -42.45f, 10.32f, -82.48f, 28.62f, -117.73f)
                horizontalLineToRelative(0.01f)
                lineTo(86.63f, 148.9f)
                lineToRelative(25.4f, 57.64f)
                curveToRelative(-5.32f, 15.5f, -8.22f, 32.14f, -8.22f, 49.46f)
                curveToRelative(0f, 18.79f, 3.41f, 36.8f, 9.65f, 53.41f)
                close()
            }
            path(fill = SolidColor(Color(0xFF518EF8))) {
                moveTo(507.53f, 208.18f)
                curveTo(510.47f, 223.66f, 512f, 239.65f, 512f, 256f)
                curveToRelative(0f, 18.33f, -1.93f, 36.21f, -5.6f, 53.45f)
                curveToRelative(-12.46f, 58.68f, -45.03f, 109.93f, -90.13f, 146.19f)
                lineToRelative(-0.01f, -0.01f)
                lineToRelative(-73.04f, -3.73f)
                lineToRelative(-10.34f, -64.54f)
                curveToRelative(29.93f, -17.55f, 53.32f, -45.03f, 65.65f, -77.91f)
                horizontalLineToRelative(-136.89f)
                verticalLineTo(208.18f)
                horizontalLineToRelative(245.9f)
                close()
            }
            path(fill = SolidColor(Color(0xFF28B446))) {
                moveTo(416.25f, 455.62f)
                lineToRelative(0.01f, 0.01f)
                curveTo(372.4f, 490.9f, 316.67f, 512f, 256f, 512f)
                curveToRelative(-97.49f, 0f, -182.25f, -54.49f, -225.49f, -134.68f)
                lineToRelative(82.96f, -67.91f)
                curveToRelative(21.62f, 57.7f, 77.28f, 98.77f, 142.53f, 98.77f)
                curveToRelative(28.05f, 0f, 54.32f, -7.58f, 76.87f, -20.82f)
                lineToRelative(83.38f, 68.26f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF14336))) {
                moveTo(419.4f, 58.94f)
                lineToRelative(-82.93f, 67.9f)
                curveTo(313.14f, 112.25f, 285.55f, 103.82f, 256f, 103.82f)
                curveToRelative(-66.73f, 0f, -123.43f, 42.96f, -143.96f, 102.72f)
                lineToRelative(-83.4f, -68.28f)
                horizontalLineToRelative(-0.01f)
                curveTo(71.23f, 56.12f, 157.06f, 0f, 256f, 0f)
                curveToRelative(62.12f, 0f, 119.07f, 22.13f, 163.4f, 58.94f)
                close()
            }
        }.build()
    }

    val Apple: ImageVector by lazy {
        ImageVector.Builder(
            name = "AppleIcon",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 22.773f,
            viewportHeight = 22.773f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(15.769f, 0f)
                horizontalLineToRelative(0.162f)
                curveToRelative(0.13f, 1.606f, -0.483f, 2.806f, -1.228f, 3.675f)
                curveToRelative(-0.731f, 0.863f, -1.732f, 1.7f, -3.351f, 1.573f)
                curveToRelative(-0.108f, -1.583f, 0.506f, -2.694f, 1.25f, -3.561f)
                curveTo(13.292f, 0.879f, 14.557f, 0.16f, 15.769f, 0f)
                close()
                moveTo(20.67f, 16.716f)
                verticalLineToRelative(0.045f)
                curveToRelative(-0.455f, 1.378f, -1.104f, 2.559f, -1.896f, 3.655f)
                curveToRelative(-0.723f, 0.995f, -1.609f, 2.334f, -3.191f, 2.334f)
                curveToRelative(-1.367f, 0f, -2.275f, -0.879f, -3.676f, -0.903f)
                curveToRelative(-1.482f, -0.024f, -2.297f, 0.735f, -3.652f, 0.926f)
                horizontalLineToRelative(-0.462f)
                curveToRelative(-0.995f, -0.144f, -1.798f, -0.932f, -2.383f, -1.642f)
                curveToRelative(-1.725f, -2.098f, -3.058f, -4.808f, -3.306f, -8.276f)
                verticalLineToRelative(-1.019f)
                curveToRelative(0.105f, -2.482f, 1.311f, -4.5f, 2.914f, -5.478f)
                curveToRelative(0.846f, -0.52f, 2.009f, -0.963f, 3.304f, -0.765f)
                curveToRelative(0.555f, 0.086f, 1.122f, 0.276f, 1.619f, 0.464f)
                curveToRelative(0.471f, 0.181f, 1.06f, 0.502f, 1.618f, 0.485f)
                curveToRelative(0.378f, -0.011f, 0.754f, -0.208f, 1.135f, -0.347f)
                curveToRelative(1.116f, -0.403f, 2.21f, -0.865f, 3.652f, -0.648f)
                curveToRelative(1.733f, 0.262f, 2.963f, 1.032f, 3.723f, 2.22f)
                curveToRelative(-1.466f, 0.933f, -2.625f, 2.339f, -2.427f, 4.74f)
                curveToRelative(0.176f, 2.181f, 1.444f, 3.457f, 3.028f, 4.209f)
                close()
            }
        }.build()
    }

    val Email: ImageVector by lazy {
        ImageVector.Builder(
            name = "EmailIcon",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 32f,
            viewportHeight = 32f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(31.348f, 13.8f)
                arcTo(15.5f, 15.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.627f, 18.015f)
                arcToRelative(15.614f, 15.614f, 0f, isMoreThanHalf = false, isPositiveArc = false, 13.31f, 13.351f)
                arcToRelative(16.058f, 16.058f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.08f, 0.136f)
                arcToRelative(15.351f, 15.351f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7.972f, -2.217f)
                arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.548f, -2.57f)
                arcToRelative(12.5f, 12.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4.789f, -23.109f)
                arcToRelative(12.5f, 12.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.162f, 16.488f)
                arcToRelative(2.166f, 2.166f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.079f, 1.406f)
                arcToRelative(2.238f, 2.238f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.235f, -2.235f)
                verticalLineTo(10f)
                arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -3f, 0f)
                verticalLineToRelative(0.014f)
                arcToRelative(7.5f, 7.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0.541f, 11.523f)
                arcToRelative(5.224f, 5.224f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4.694f, 2.963f)
                arcToRelative(5.167f, 5.167f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4.914f, -3.424f)
                arcToRelative(15.535f, 15.535f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.699f, -7.276f)
                close()
                moveTo(16f, 20.5f)
                arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4.5f, -4.5f)
                arcToRelative(4.505f, 4.505f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.5f, 4.5f)
                close()
            }
        }.build()
    }
}
