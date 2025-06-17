package com.example.ouralbum.presentation.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ouralbum.OurAlbumNavHost
import com.example.ouralbum.presentation.component.CustomBottomNavigationBar

@Composable
fun MainScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                currentRoute = currentDestination?.route
            )
        }
    ) { innerPadding ->
        OurAlbumNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
