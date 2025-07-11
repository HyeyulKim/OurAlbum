package com.example.ouralbum.presentation.screen.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.presentation.component.PhotoCard

@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "My Album"
            )
        }
    ) { paddingValues ->
        if (isLoggedIn) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(uiState.photos) { photo ->
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
