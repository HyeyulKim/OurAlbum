package com.example.ouralbum

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ouralbum.presentation.navigation.NavigationItem
import com.example.ouralbum.presentation.navigation.Routes
import com.example.ouralbum.presentation.screen.bookmark.BookmarkScreen
import com.example.ouralbum.presentation.screen.detail.PhotoDetailScreen
import com.example.ouralbum.presentation.screen.detail.PhotoEditScreen
import com.example.ouralbum.presentation.screen.gallery.GalleryScreen
import com.example.ouralbum.presentation.screen.home.HomeScreen
import com.example.ouralbum.presentation.screen.login.LoginScreen
import com.example.ouralbum.presentation.screen.mypage.MyPageRoute
import com.example.ouralbum.presentation.screen.write.WriteScreen
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

    // 상세/수정 라우트 (Routes에 상수 추가 권장)
    val photoDetailRoute = "photo/{photoId}"
    val photoEditRoute = "photo/{photoId}/edit"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 로그인
        composable(Routes.Login) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 홈 (→ 상세 이동)
        composable(NavigationItem.Home.route) {
            HomeScreen(
                onOpenDetail = { photoId ->
                    navController.navigate(photoDetailRoute.replace("{photoId}", photoId))
                }
            )
        }

        // 갤러리 (→ 상세 이동)
        composable(NavigationItem.Gallery.route) {
            GalleryScreen(
                onOpenDetail = { photoId ->
                    navController.navigate(photoDetailRoute.replace("{photoId}", photoId))
                }
            )
        }

        // 글쓰기
        composable(NavigationItem.Write.route) {
            WriteScreen()
        }

        // 북마크
        composable(NavigationItem.Bookmark.route) {
            BookmarkScreen(
                onOpenDetail = { photoId ->
                    navController.navigate(photoDetailRoute.replace("{photoId}", photoId))
                }
            )
        }

        // 마이페이지
        composable(NavigationItem.MyPage.route) {
            MyPageRoute(navController = navController)
        }

        // 상세
        composable(
            route = photoDetailRoute,
            arguments = listOf(navArgument("photoId") { type = NavType.StringType })
        ) {
            PhotoDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { pid ->
                    navController.navigate(photoEditRoute.replace("{photoId}", pid))
                }
            )
        }

        // 수정
        composable(
            route = photoEditRoute,
            arguments = listOf(navArgument("photoId") { type = NavType.StringType })
        ) {
            PhotoEditScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
