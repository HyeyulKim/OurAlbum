package com.example.ouralbum.domain.model

/**
 * 목록(Home/Gallery) 전용 가벼운 모델.
 * - 상세 정보(작성자, storagePath 등)는 PhotoDetail에서 관리
 */
data class Photo(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val imageUrl: String = "",
    val isBookmarked: Boolean = false,
    val createdAt: Long
)
