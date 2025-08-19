package com.example.ouralbum.presentation.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ouralbum.OurAlbumNavHost
import com.example.ouralbum.presentation.component.CustomBottomNavigationBar
import com.example.ouralbum.presentation.navigation.NavigationItem

@Composable
fun MainScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 바텀바를 보여줄 라우트(탭)만 정의
    val bottomBarRoutes = remember {
        setOf(
            NavigationItem.Home.route,
            NavigationItem.Gallery.route,
            NavigationItem.Write.route,
            NavigationItem.Bookmark.route,
            NavigationItem.MyPage.route
        )
    }
    val showBottomBar = currentRoute in bottomBarRoutes
    // 로그인/온보딩 등은 바텀바 숨김: currentRoute == Routes.Login 일 때 false
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CustomBottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        OurAlbumNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}