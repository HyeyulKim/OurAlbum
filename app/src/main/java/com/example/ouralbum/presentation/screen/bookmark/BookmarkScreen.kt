package com.example.ouralbum.presentation.screen.bookmark

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.presentation.component.PhotoCard
import com.example.ouralbum.ui.util.Dimension

@Composable
fun BookmarkScreen(viewModel: BookmarkViewModel = hiltViewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val spacingTiny = Dimension.scaledWidth(0.001f)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Bookmark"
            )
        }
    ) { paddingValues ->
        if (isLoggedIn) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(spacingTiny),
                verticalArrangement = Arrangement.spacedBy(spacingTiny),
                horizontalArrangement = Arrangement.spacedBy(spacingTiny)
            ) {
                items(uiState.bookmarkedPhotos) { photo ->
                    PhotoCard(
                        photo = photo,
                        onBookmarkClick = { viewModel.onBookmarkClick(photo.id) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인이 필요합니다",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
