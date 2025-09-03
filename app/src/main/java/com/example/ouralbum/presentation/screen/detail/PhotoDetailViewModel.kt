package com.example.ouralbum.presentation.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.usecase.DeletePhotoUseCase
import com.example.ouralbum.domain.usecase.GetPhotoDetailUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val photoId: String = checkNotNull(savedStateHandle["photoId"])
    private val _uiState = MutableStateFlow(PhotoDetailUiState())
    val uiState: StateFlow<PhotoDetailUiState> = _uiState

    private val _commentUiState = MutableStateFlow(CommentUiState())
    val commentUiState: StateFlow<CommentUiState> = _commentUiState

    private var commentListener: ListenerRegistration? = null

    private var subJob: Job? = null
    val myUid: String? get() = auth.currentUser?.uid

    // 북마크 상태용: UI에서 아이콘/버튼 상태
    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked

    // 북마크 문서 리스너
    private var bookmarkListener: ListenerRegistration? = null

    // 중복 클릭 방지
    private var isTogglingBookmark = false

    init { subscribe() }

    fun reload() = subscribe()

    private fun subscribe() {
        subJob?.cancel()
        subJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getPhotoDetailUseCase(photoId).collect { detail ->
                val uid = auth.currentUser?.uid
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        photo = detail,
                        isOwner = (detail?.userId == uid),
                        error = null
                    )
                }
                if (detail != null) {
                    prefetchCommentCount(detail.id)
                    attachBookmarkState(detail.id) // 북마크 상태 구독 시작
                }
            }
        }
    }

    private fun prefetchCommentCount(photoId: String) {
        firestore.collection("photos").document(photoId)
            .collection("comments")
            .count()
            .get(AggregateSource.SERVER)
            .addOnSuccessListener { agg ->
                _commentUiState.update { it.copy(count = agg.count.toInt()) }
            }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                deletePhotoUseCase(photoId) // Storage 이미지 삭제까지 내부에서 수행
                onDeleted()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "삭제 실패") }
            }
        }
    }

    fun toggleComments(photoId: String) {
        val nowOpen = !_commentUiState.value.opened
        if (nowOpen) {
            // 열기
            _commentUiState.update { it.copy(opened = true, loading = true, photoId = photoId) }
            attachComments(photoId)
        } else {
            // 닫기
            detachComments()
            _commentUiState.update { it.copy(opened = false, loading = false, input = "") }
        }
    }

    private fun attachComments(photoId: String) {
        commentListener?.remove()
        commentListener = firestore.collection("photos").document(photoId)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    _commentUiState.update { it.copy(loading = false) }
                    return@addSnapshotListener
                }
                val list = snap?.documents?.map { d ->
                    CommentItem(
                        id = d.id,
                        text = d.getString("text") ?: "",
                        authorName = d.getString("authorName"),
                        userId = d.getString("userId") ?: "",
                        createdAt = d.getTimestamp("createdAt")
                    )
                }.orEmpty()
                _commentUiState.update { it.copy(loading = false, comments = list, count = list.size) }
            }
    }

    private fun detachComments() {
        commentListener?.remove()
        commentListener = null
    }

    fun onCommentInputChange(s: String) {
        _commentUiState.update { it.copy(input = s) }
    }

    fun sendComment() {
        val state = _commentUiState.value
        val uid = auth.currentUser?.uid ?: return
        val pid = state.photoId ?: return
        val text = state.input.trim()
        if (text.isBlank() || state.sending) return

        _commentUiState.update { it.copy(sending = true) }

        val data = hashMapOf(
            "text" to text,
            "userId" to uid,
            "authorName" to (auth.currentUser?.displayName ?: ""),
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("photos").document(pid)
            .collection("comments")
            .add(data)
            .addOnSuccessListener {
                _commentUiState.update { it.copy(sending = false, input = "") }
            }
            .addOnFailureListener {
                _commentUiState.update { it.copy(sending = false) }
            }
    }

    fun updateComment(commentId: String, newText: String) {
        val pid = _commentUiState.value.photoId ?: return
        firestore.collection("photos").document(pid)
            .collection("comments").document(commentId)
            .update(mapOf("text" to newText))
            .addOnFailureListener { e ->
                android.util.Log.e("UpdateComment", "Failed to update comment", e)
                _uiState.update { it.copy(error = e.message ?: "댓글 수정 실패") }
            }
    }

    fun deleteComment(commentId: String) {
        val pid = _commentUiState.value.photoId ?: return
        firestore.collection("photos").document(pid)
            .collection("comments").document(commentId)
            .delete()
            .addOnFailureListener { e ->
                android.util.Log.e("DeleteComment", "Failed to delete comment", e)
                _uiState.update { it.copy(error = e.message ?: "댓글 삭제 실패") }
            }
    }

    // 예외 처리 + 중복 클릭 방지 + 낙관적 UI 반영
    fun onBookmarkClick(photoId: String) {
        val uid = auth.currentUser?.uid ?: return
        if (isTogglingBookmark) return
        isTogglingBookmark = true

        // 낙관적 토글: 즉시 반영 후 실패 시 롤백
        val before = _isBookmarked.value
        _isBookmarked.value = !before

        viewModelScope.launch {
            runCatching { toggleBookmarkUseCase(photoId) }
                .onFailure { e ->
                    // 롤백 & 에러 표기
                    _isBookmarked.value = before
                    _uiState.update { it.copy(error = e.message ?: "북마크 처리에 실패했습니다.") }
                }
            isTogglingBookmark = false
        }
    }

    // 북마크 상태 구독: users/{uid}/bookmarks/{photoId} 존재 여부
    private fun attachBookmarkState(photoId: String) {
        bookmarkListener?.remove()
        val uid = auth.currentUser?.uid ?: run {
            _isBookmarked.value = false
            return
        }
        bookmarkListener = firestore.collection("users")
            .document(uid)
            .collection("bookmarks")
            .document(photoId)
            .addSnapshotListener { snap, e ->
                if (e != null) return@addSnapshotListener
                _isBookmarked.value = (snap?.exists() == true)
            }
    }

    private fun detachBookmark() {
        bookmarkListener?.remove()
        bookmarkListener = null
    }

    override fun onCleared() {
        super.onCleared()
        detachComments()
        detachBookmark()
    }
}
