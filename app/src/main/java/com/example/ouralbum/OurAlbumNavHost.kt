package com.example.ouralbum

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ouralbum.presentation.screen.bookmark.BookmarkScreen
import com.example.ouralbum.presentation.screen.gallery.GalleryScreen
import com.example.ouralbum.presentation.screen.home.HomeScreen
import com.example.ouralbum.presentation.screen.write.WriteScreen
import com.example.ouralbum.presentation.navigation.NavigationItem
import com.example.ouralbum.presentation.screen.login.LoginScreen
import com.example.ouralbum.presentation.screen.mypage.MyPageRoute

@Composable
fun OurAlbumNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
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
            MyPageRoute(navController = navController)
        }

        // 로그인 화면 추가
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}

