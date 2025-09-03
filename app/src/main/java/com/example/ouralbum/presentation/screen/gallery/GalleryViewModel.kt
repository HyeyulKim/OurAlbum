package com.example.ouralbum.presentation.screen.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.auth.AuthStateProvider
import com.example.ouralbum.domain.usecase.GetUserPhotosUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getUserPhotosUseCase: GetUserPhotosUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    authStateProvider: AuthStateProvider,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
    ) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authStateProvider.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // 갤러리 UI 상태
    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState

    // 내가 북마크한 photoId 집합 (UI에서 아이콘 상태 판단용)
    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds

    // 내부 상태
    private var isLoadingPhotos = false
    private val toggling = mutableSetOf<String>()        // 토글 중복 클릭 방지
    private var bookmarkListener: ListenerRegistration? = null // 리스너 해제용

    init {
        // 로그인 상태 변화에 반응해 사진 로드/초기화
        viewModelScope.launch {
            isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    loadUserPhotos()
                    attachBookmarks()
                } else {
                    // 로그아웃시 화면 초기화
                    detachBookmarks()
                    _bookmarkedIds.value = emptySet()
                    _uiState.value = GalleryUiState()
                }
            }
        }
    }

    private fun loadUserPhotos() {
        if (isLoadingPhotos) return
        isLoadingPhotos = true
        viewModelScope.launch {
            getUserPhotosUseCase()
                .onStart {
                    _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "사진 로드 실패"
                    )
                    isLoadingPhotos = false
                }
                .collect { photos ->
                    _uiState.value = _uiState.value.copy(
                        photos = photos,
                        isLoading = false,
                        error = null
                    )
                    isLoadingPhotos = false
                }
        }
    }

    fun reload() {
        if (isLoggedIn.value) loadUserPhotos()// 로그인 시에만 재시도
    }

    fun onBookmarkClick(photoId: String) {
        if (!isLoggedIn.value || photoId.isBlank()) return
        if (!toggling.add(photoId)) return

        // 1) 낙관적 업데이트
        val before = _bookmarkedIds.value
        val optimistic = before.toMutableSet().apply {
            if (!add(photoId)) remove(photoId)
        }
        _bookmarkedIds.value = optimistic

        // 2) 서버 반영
        viewModelScope.launch {
            runCatching { toggleBookmarkUseCase(photoId) }
                .onFailure { e ->
                    // 3) 실패 시 롤백
                    _bookmarkedIds.value = before
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "북마크 처리에 실패했습니다."
                    )
                }
            // 4) 중복 방지 해제
            toggling.remove(photoId)
        }
    }

    // 내 북마크 상태 실시간 구독: users/{uid}/bookmarks
    private fun attachBookmarks() {
        bookmarkListener?.remove()
        val uid = auth.currentUser?.uid ?: run {
            _bookmarkedIds.value = emptySet()
            return
        }
        bookmarkListener = firestore.collection("users")
            .document(uid)
            .collection("bookmarks")
            .addSnapshotListener { snap, e ->
                if (e != null) return@addSnapshotListener
                _bookmarkedIds.value = snap?.documents?.map { it.id }?.toSet() ?: emptySet()
            }
    }

    private fun detachBookmarks() {
        bookmarkListener?.remove()
        bookmarkListener = null
    }

    override fun onCleared() {
        super.onCleared()
        detachBookmarks()
    }
}