package com.example.ouralbum.presentation.screen.detail

data class PhotoEditUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val content: String = "",
    val error: String? = null,
    val done: Boolean = false
)
