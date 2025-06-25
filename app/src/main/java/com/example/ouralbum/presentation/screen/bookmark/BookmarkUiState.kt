package com.example.ouralbum.presentation.screen.bookmark

import com.example.ouralbum.domain.model.Photo

data class BookmarkUiState(
    val bookmarkedPhotos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
