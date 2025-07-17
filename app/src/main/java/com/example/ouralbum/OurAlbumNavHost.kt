package com.example.ouralbum

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ouralbum.presentation.screen.bookmark.BookmarkScreen
import com.example.ouralbum.presentation.screen.gallery.GalleryScreen
import com.example.ouralbum.presentation.screen.home.HomeScreen
import com.example.ouralbum.presentation.screen.mypage.MyPageScreen
import com.example.ouralbum.presentation.screen.write.WriteScreen
import com.example.ouralbum.presentation.navigation.NavigationItem
import com.example.ouralbum.presentation.screen.login.LoginScreen

@Composable
fun OurAlbumNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // 로그인 상태 관리
    var isLoggedIn by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("비회원") }
    var userEmail by remember { mutableStateOf("로그인이 필요합니다") }

    NavHost(
        navController = navController,
        startDestination = NavigationItem.Home.route,
        modifier = modifier
    ) {
        composable(NavigationItem.Home.route) {
            HomeScreen()
        }
        composable(NavigationItem.Gallery.route) {
            GalleryScreen()
        }
        composable(NavigationItem.Write.route) {
            WriteScreen()
        }
        composable(NavigationItem.Bookmark.route) {
            BookmarkScreen()
        }
        composable(NavigationItem.MyPage.route) {
            MyPageScreen(
                isLoggedIn = isLoggedIn, // 로그인 상태 전달
                userName = userName,
                userEmail = userEmail,
                onLoginClick = {
                    // 로그인 화면으로 이동
                    navController.navigate("login")
                },
                onLogoutClick = {
                    // 로그아웃 후 로그인 화면으로 이동
                    isLoggedIn = false
                    userName = "비회원"
                    userEmail = "로그인이 필요합니다"
                    navController.navigate("login")
                },
                navController = navController
            )
        }

        // 로그인 화면 추가
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { userInfo ->
                    isLoggedIn = true
                    userName = userInfo.displayName
                    userEmail = userInfo.email
                }
            )
        }
    }
}

