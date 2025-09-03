package com.example.ouralbum.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ouralbum.presentation.component.AppTopBar
import com.example.ouralbum.presentation.component.PhotoCard
import com.example.ouralbum.presentation.component.ErrorView
import com.example.ouralbum.presentation.component.EmptyView
import com.example.ouralbum.presentation.component.LoginRequiredView

@Composable
fun HomeScreen(
    onOpenDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(title = "Our Album") }
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

                uiState.photos.isEmpty() -> {
                    EmptyView("피드에 게시물이 없습니다.")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.photos, key = { it.id }) { photo ->
                            PhotoCard(
                                photo = photo,
                                bookmarked = bookmarkedIds.contains(photo.id),
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