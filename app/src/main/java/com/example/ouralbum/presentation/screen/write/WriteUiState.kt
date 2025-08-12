package com.example.ouralbum.presentation.screen.write

import android.net.Uri

data class WriteUiState(
    val title: String = "",
    val content: String = "",
    val imageUri: Uri? = null
)
