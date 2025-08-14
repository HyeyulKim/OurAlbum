package com.example.ouralbum.presentation.screen.mypage

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

@Composable
fun MyPageRoute(
    navController: NavHostController,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val isLoggedIn = viewModel.isLoggedIn.collectAsStateWithLifecycle().value
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    MyPageScreen(
        isLoggedIn = isLoggedIn,
        userName = uiState.name.ifBlank { "비회원" },
        userEmail = uiState.email.ifBlank { "로그인이 필요합니다" },
        onLogoutClick = { viewModel.logout() },
        navController = navController
    )
}
