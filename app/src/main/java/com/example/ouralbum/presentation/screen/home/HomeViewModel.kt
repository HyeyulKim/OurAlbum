package com.example.ouralbum.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.auth.AuthStateProvider
import com.example.ouralbum.domain.usecase.GetPhotosUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    authStateProvider: AuthStateProvider,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    // 로그인 상태 (GalleryViewModel과 동일 패턴)
    val isLoggedIn: StateFlow<Boolean> = authStateProvider.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // UI 상태
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    // 내가 북마크한 photoId 집합 (아이콘 상태에 사용)
    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds

    // 중복 로딩 방지 플래그
    private var isLoadingPhotos = false
    // 중복 클릭 방지용 플래그
    private val toggling = mutableSetOf<String>()
    // 북마크 컬렉션 리스너
    private var bookmarkListener: ListenerRegistration? = null

    // 최초 구독: 로그인되면 로드 및 북마크 상태 구독, 로그아웃되면 초기화/해제
    init {
        viewModelScope.launch {
            isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    loadPhotos()
                    attachBookmarks()
                } else {
                    detachBookmarks()
                    _bookmarkedIds.value = emptySet()
                    _uiState.value = HomeUiState()
                }
            }
        }
    }

    private fun loadPhotos() {
        if (isLoadingPhotos) return
        isLoadingPhotos = true
        viewModelScope.launch {
            getPhotosUseCase()
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
        if (isLoggedIn.value) loadPhotos() // 로그인 시에만 재시도
    }

    fun onBookmarkClick(photoId: String) {
        if (!isLoggedIn.value || photoId.isBlank()) return
        if (!toggling.add(photoId)) return

        // 1) 낙관적 업데이트: 즉시 토글
        val before = _bookmarkedIds.value
        val optimistic = before.toMutableSet().apply {
            if (!add(photoId)) remove(photoId)
        }
        _bookmarkedIds.value = optimistic

        // 2) 서버 반영
        viewModelScope.launch {
            runCatching { toggleBookmarkUseCase(photoId) }
                .onFailure { e ->
                    // 3) 실패 시 롤백 + 에러 표시
                    _bookmarkedIds.value = before
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "북마크 처리에 실패했습니다."
                    )
                }
            // 4) 중복 방지 해제
            toggling.remove(photoId)
        }
    }

    // 내 북마크 상태 스냅샷 구독: users/{uid}/bookmarks
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

    // 리스너 해제
    private fun detachBookmarks() {
        bookmarkListener?.remove()
        bookmarkListener = null
    }

    override fun onCleared() {
        super.onCleared()
        detachBookmarks()
    }

}
