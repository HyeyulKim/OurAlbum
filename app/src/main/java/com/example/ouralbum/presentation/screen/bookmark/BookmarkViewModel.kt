package com.example.ouralbum.presentation.screen.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.auth.AuthStateProvider
import com.example.ouralbum.domain.usecase.GetBookmarkedPhotosUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookmarkedPhotosUseCase: GetBookmarkedPhotosUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    authStateProvider: AuthStateProvider
) : ViewModel() {

    // 공통 로그인 상태: SSOT
    val isLoggedIn: StateFlow<Boolean> = authStateProvider.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _uiState = MutableStateFlow(BookmarkUiState())
    val uiState: StateFlow<BookmarkUiState> = _uiState

    init {
        // 로그인 변화에 반응
        viewModelScope.launch {
            isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    loadBookmarkedPhotos()
                } else {
                    // 로그아웃 시 화면 초기화
                    _uiState.value = BookmarkUiState()
                }
            }
        }
    }

    // 중복 로딩 방지
    private var isLoading = false

    private fun loadBookmarkedPhotos() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            getBookmarkedPhotosUseCase()
                .onStart {
                    _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                    isLoading = false
                }
                .collect { photos ->
                    _uiState.value = _uiState.value.copy(
                        bookmarkedPhotos = photos,
                        isLoading = false,
                        error = null
                    )
                    isLoading = false
                }
        }
    }

    fun onBookmarkClick(photoId: String) {
        viewModelScope.launch {
            toggleBookmarkUseCase(photoId)
            if (isLoggedIn.value) loadBookmarkedPhotos()
        }
    }
}