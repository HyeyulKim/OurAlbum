package com.example.ouralbum.domain.model

data class PhotoDetail(
    val id: String,
    val title: String,
    val content: String,
    val date: String,
    val imageUrl: String,

    // 상세 전용 필드
    val userId: String,          // 글 작성자 식별 (수정/삭제 버튼 노출 판단)
    val storagePath: String? = null // 선택: 문서에 별도로 저장 시 사용, 없으면 imageUrl로 Storage 참조 삭제
)
