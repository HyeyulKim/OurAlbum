package com.example.ouralbum.presentation.screen.detail

import com.example.ouralbum.domain.model.PhotoDetail

data class PhotoDetailUiState(
    val isLoading: Boolean = true,
    val photo: PhotoDetail? = null,
    val isOwner: Boolean = false,
    val error: String? = null
)