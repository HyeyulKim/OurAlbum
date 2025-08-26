package com.example.ouralbum.presentation.screen.detail

data class CommentUiState(
    val opened: Boolean = false,
    val loading: Boolean = false,
    val sending: Boolean = false,
    val input: String = "",
    val comments: List<CommentItem> = emptyList(),
    val count: Int = 0,
    val photoId: String? = null
)