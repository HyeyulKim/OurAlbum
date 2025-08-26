package com.example.ouralbum.presentation.screen.detail

data class CommentItem(
    val id: String,
    val text: String,
    val authorName: String?,
    val userId: String,
    val createdAt: com.google.firebase.Timestamp?
) {
    val createdAtText: String =
        createdAt?.toDate()?.let { java.text.SimpleDateFormat("yyyy.MM.dd HH:mm", java.util.Locale.getDefault()).format(it) }
            ?: ""
}
