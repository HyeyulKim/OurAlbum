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

    // 토글 중복 클릭 방지 (photoId별)
    private val toggling = mutableSetOf<String>()

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

    fun reload() {
        // 로그인일 때만 재시도 허용
        if (isLoggedIn.value) loadBookmarkedPhotos()
    }

    fun onBookmarkClick(photoId: String) {
        // 로그인 상태 체크(방어)
        if (!isLoggedIn.value || photoId.isBlank()) return
        // 토글 중복 클릭 방지
        if (!toggling.add(photoId)) return // 이미 토글 중이면 return

        viewModelScope.launch {
            // 토글 시에는 목록 로딩 스피너를 건드리지 않음 (isLoading 유지)
            runCatching { toggleBookmarkUseCase(photoId) }
                .onSuccess {
                    // 성공 시 별도 reload/load 호출 불필요
                    // GetBookmarkedPhotosUseCase 흐름이 실시간으로 반영
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        // 목록 로딩 상태는 건드리지 않음
                        error = e.message ?: "북마크 처리에 실패했습니다."
                    )
                }
            toggling.remove(photoId) // 해제
        }
    }

}