package com.example.ouralbum.presentation.screen.write

data class WriteUiState(
    val title: String = "",
    val content: String = "",
    val selectedPeople: List<String> = listOf("김철수", "최영희", "강진영")
)
