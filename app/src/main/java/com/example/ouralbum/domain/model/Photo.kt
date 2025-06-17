package com.example.ouralbum.domain.model

data class Photo(
    val id: String,
    val title: String,
    val date: String,
    val imageUrl: String,
    val isBookmarked: Boolean
)
