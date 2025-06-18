package com.example.ouralbum.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.ouralbum.ui.util.Dimension

@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    heightPercent: Float = 0.06f,
    fontPercent: Float = 0.025f,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    containerColor: Color = MaterialTheme.colorScheme.background
) {
    val height = Dimension.scaledHeight(heightPercent)
    val fontSize = Dimension.scaledFont(fontPercent)

    Column {
        // 상단바 배경 + 중앙 텍스트
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = fontSize,
                style = style.copy(fontSize = fontSize),
                maxLines = 1
            )
        }

        if (showDivider) {
            AppDivider()
        }
    }
}
