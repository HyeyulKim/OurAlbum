package com.example.ouralbum.domain.model

/**
 * 목록(Home/Gallery) 전용 가벼운 모델.
 * - 상세 정보(작성자, storagePath 등)는 PhotoDetail에서 관리
 * - 프로필 아바타/표시용 메타 필드 포함
 */
data class Photo(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val imageUrl: String = "",
    val createdAt: Long = 0L,

    // 작성자 메타
    val authorName: String? = null,          // 구글 닉네임
    val authorPhotoUrl: String? = null       // 구글 프로필 이미지
)
