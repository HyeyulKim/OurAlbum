package com.example.ouralbum.presentation.screen.bookmark

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ouralbum.ui.util.Dimension

@Composable
fun BookmarkScreen(
    onOpenDetail: (String) -> Unit,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val spacingTiny = Dimension.scaledWidth(0.001f)

    Scaffold(
        topBar = { AppTopBar(title = "Bookmark") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                !isLoggedIn -> {
                    LoginRequiredView()
                }

                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error ?: "알 수 없는 오류가 발생했습니다.",
                        onRetry = { viewModel.reload() }
                    )
                }

                uiState.bookmarkedPhotos.isEmpty() -> {
                    EmptyView("북마크된 게시물이 없습니다.")
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(spacingTiny),
                        verticalArrangement = Arrangement.spacedBy(spacingTiny),
                        horizontalArrangement = Arrangement.spacedBy(spacingTiny)
                    ) {
                        items(uiState.bookmarkedPhotos, key = { it.id }) { photo ->
                            PhotoCard(
                                photo = photo,
                                onBookmarkClick = { viewModel.onBookmarkClick(photo.id) },
                                onClick = { onOpenDetail(photo.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}