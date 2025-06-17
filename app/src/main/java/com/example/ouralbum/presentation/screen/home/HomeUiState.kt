package com.example.ouralbum.presentation.screen.home

import com.example.ouralbum.domain.model.Photo

data class HomeUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
