package com.example.ouralbum.presentation.screen.mypage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ouralbum.presentation.component.AppDivider
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.ui.util.Dimension

@Composable
fun MyPageScreen(
    userName: String = "김혜율",
    userEmail: String = "hyeyul102500@gmail.com",
    onFriendListClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "My Page"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 사용자 정보
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = userName, style = MaterialTheme.typography.bodyLarge)
                Text(text = userEmail, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            AppDivider()

            // 친구 목록
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFriendListClick() }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "친구목록", style = MaterialTheme.typography.bodyLarge)
                Icon(Icons.Outlined.ArrowForwardIos,
                    contentDescription = "친구 목록 이동",
                    modifier = Modifier.size(Dimension.scaledWidth(0.035f)),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            }

            AppDivider()

            // 로그아웃
            Text(
                text = "로그아웃",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogoutClick() }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            AppDivider()
        }
    }
}
