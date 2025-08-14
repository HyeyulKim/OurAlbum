package com.example.ouralbum.presentation.screen.mypage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ouralbum.presentation.component.AppDivider
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.ui.util.Dimension

@Composable
fun MyPageScreen(
    isLoggedIn: Boolean,
    userName: String = "비회원",
    userEmail: String = "로그인이 필요합니다",
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "My Page")
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

            // 로그인 / 로그아웃 버튼
            Text(
                text = if (isLoggedIn) "로그아웃" else "로그인",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isLoggedIn) {
                            onLogoutClick()
                        } else {
                            // 로그인 버튼 클릭 시 LoginScreen으로 이동
                            navController.navigate("login")
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            AppDivider()
        }
    }
}
