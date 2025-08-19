package com.example.ouralbum.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.auth.AuthStateProvider
import com.example.ouralbum.domain.usecase.GetPhotosUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import com.google.firebase.auth.FirebaseAuth
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
    authStateProvider: AuthStateProvider
) : ViewModel() {

    // 로그인 상태 (GalleryViewModel과 동일 패턴)
    val isLoggedIn: StateFlow<Boolean> = authStateProvider.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // UI 상태
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    // 최초 구독: 로그인되면 로드, 로그아웃되면 초기화
    init {
        viewModelScope.launch {
            isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    loadPhotos()
                } else {
                    _uiState.value = HomeUiState() // 로그아웃 시 초기화
                }
            }
        }
    }

    // 중복 로딩 방지 플래그
    private var isLoadingPhotos = false

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
        viewModelScope.launch {
            toggleBookmarkUseCase(photoId)
            reload() // 북마크 후 새로고침
        }
    }
}
