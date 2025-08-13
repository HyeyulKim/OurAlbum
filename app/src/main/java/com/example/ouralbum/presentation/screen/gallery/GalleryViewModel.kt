package com.example.ouralbum.presentation.screen.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.usecase.GetUserPhotosUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getUserPhotosUseCase: GetUserPhotosUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    // FirebaseAuth 상태를 Flow로 래핑
    private val authStateFlow: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        // 초기값도 즉시 전파
        trySend(auth.currentUser != null)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    // 화면에서 바로 구독 가능한 StateFlow<Boolean>
    val isLoggedIn: StateFlow<Boolean> = authStateFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    // 갤러리 UI 상태
    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState

    init {
        // 로그인 상태 변화에 반응해 사진 로드/초기화
        viewModelScope.launch {
            isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    loadUserPhotos()
                } else {
                    // 로그아웃시 화면 초기화
                    _uiState.value = GalleryUiState()
                }
            }
        }
    }

    // 중복 로딩 방지 (옵션)
    private var isLoadingPhotos = false

    private fun loadUserPhotos() {
        if (isLoadingPhotos) return
        isLoadingPhotos = true
        viewModelScope.launch {
            getUserPhotosUseCase()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, error = null) }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "사진 로드 실패")
                    isLoadingPhotos = false
                }
                .collect { photos ->
                    _uiState.value = _uiState.value.copy(photos = photos, isLoading = false, error = null)
                    isLoadingPhotos = false
                }
        }
    }

    fun reload() {
        // 로그인일 때만 재시도 허용
        if (isLoggedIn.value) loadUserPhotos()
    }

    fun onBookmarkClick(photoId: String) {
        viewModelScope.launch {
            toggleBookmarkUseCase(photoId)
            reload() // 북마크 후 재로딩
        }
    }
}