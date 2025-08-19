package com.example.ouralbum

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ouralbum.presentation.screen.bookmark.BookmarkScreen
import com.example.ouralbum.presentation.screen.gallery.GalleryScreen
import com.example.ouralbum.presentation.screen.home.HomeScreen
import com.example.ouralbum.presentation.screen.write.WriteScreen
import com.example.ouralbum.presentation.navigation.NavigationItem
import com.example.ouralbum.presentation.navigation.Routes
import com.example.ouralbum.presentation.screen.login.LoginScreen
import com.example.ouralbum.presentation.screen.mypage.MyPageRoute
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OurAlbumNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        NavigationItem.Home.route
    } else {
        Routes.Login
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Login) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    // 로그인 성공 시 홈으로 이동 + 로그인 스택 제거
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
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
            MyPageRoute(navController = navController)
        }
    }
}