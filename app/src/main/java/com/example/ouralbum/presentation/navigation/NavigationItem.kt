package com.example.ouralbum.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : NavigationItem("home", "", Icons.Filled.Home)
    object Gallery : NavigationItem("album", "", Icons.Filled.Photo)
    object Write : NavigationItem("write", "", Icons.Filled.AddBox)
    object Bookmark : NavigationItem("bookmark", "", Icons.Filled.Bookmark)
    object MyPage : NavigationItem("mypage", "", Icons.Filled.Person)
}
