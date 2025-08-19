// presentation/component/AppTopBar.kt
package com.example.ouralbum.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    onBack: (() -> Unit)? = null,                     // null이면 버튼 숨김
    actions: @Composable RowScope.() -> Unit = {}     // 우측 액션 슬롯
) {
    val height = Dimension.scaledHeight(heightPercent)
    val fontSize = Dimension.scaledFont(fontPercent)

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Column {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(height)
                    .background(containerColor)
                    .padding(horizontal = Dimension.paddingSmall()),
                contentAlignment = Alignment.Center
            ) {
                // 왼쪽: Back 버튼(옵션)
                if (onBack != null) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .height(height),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    }
                }

                // 중앙: 제목
                Text(
                    text = title,
                    fontSize = fontSize,
                    style = style.copy(fontSize = fontSize),
                    maxLines = 1
                )

                // 오른쪽: 액션들(옵션)
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .height(height),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }

            if (showDivider) {
                AppDivider()
            }
        }
    }
}
