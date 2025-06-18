package com.example.ouralbum.presentation.screen.gallery

import com.example.ouralbum.domain.model.Photo

data class GalleryUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
