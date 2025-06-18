package com.example.ouralbum.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Dimension {

    @Composable
    fun screenWidthDp(): Int {
        return LocalConfiguration.current.screenWidthDp
    }

    @Composable
    fun screenHeightDp(): Int {
        return LocalConfiguration.current.screenHeightDp
    }

    @Composable
    fun scaledFont(percentOfHeight: Float): TextUnit {
        val screenHeight = screenHeightDp()
        return (screenHeight * percentOfHeight).sp
    }

    @Composable
    fun scaledHeight(percentOfHeight: Float): Dp {
        val screenHeight = screenHeightDp()
        return (screenHeight * percentOfHeight).dp
    }

    @Composable
    fun scaledWidth(percentOfWidth: Float): Dp {
        val screenWidth = screenWidthDp()
        return (screenWidth * percentOfWidth).dp
    }

    @Composable
    fun paddingSmall(): Dp = scaledWidth(0.02f)  // 약 2% 좌우 여백
    @Composable
    fun paddingMedium(): Dp = scaledWidth(0.04f)
    @Composable
    fun paddingLarge(): Dp = scaledWidth(0.06f)
}
