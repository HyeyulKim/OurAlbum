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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.presentation.component.EmptyView
import com.example.ouralbum.presentation.component.ErrorView
import com.example.ouralbum.presentation.component.LoginRequiredView
import com.example.ouralbum.presentation.component.PhotoCard

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(title = "My Album") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                !isLoggedIn -> LoginRequiredView()

                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error ?: "알 수 없는 오류가 발생했습니다.",
                        onRetry = { viewModel.reload() }
                    )
                }

                else -> {
                    if (uiState.photos.isEmpty()) {
                        EmptyView("업로드된 게시물이 없습니다.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(uiState.photos, key = { it.id }) { photo ->
                                PhotoCard(
                                    photo = photo,
                                    onBookmarkClick = { viewModel.onBookmarkClick(photo.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}