package com.example.ouralbum.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.ouralbum.presentation.navigation.NavigationItem
import com.example.ouralbum.ui.util.Dimension

@Composable
fun CustomBottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Gallery,
        NavigationItem.Write,
        NavigationItem.Bookmark,
        NavigationItem.MyPage
    )
    val isDarkTheme = isSystemInDarkTheme()

    val navBarHeight = Dimension.scaledHeight(0.06f) // 예: 화면 높이의 7%
    val iconSize = Dimension.scaledWidth(0.07f) // 예: 화면 너비의 7%
    val paddingVertical = Dimension.scaledHeight(0.01f)
    val paddingHorizontal = Dimension.scaledWidth(0.02f)

    Column {
        AppDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(navBarHeight)
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                val iconTint = when {
                    selected && isDarkTheme -> Color.White
                    selected && !isDarkTheme -> Color.Black
                    else -> Color.Gray
                }
                Box(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            if (!selected) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(vertical = paddingVertical, horizontal = paddingHorizontal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconTint,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}
