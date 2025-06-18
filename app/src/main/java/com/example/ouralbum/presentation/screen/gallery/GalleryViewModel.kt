package com.example.ouralbum.presentation.screen.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.usecase.GetUserPhotosUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getUserPhotosUseCase: GetUserPhotosUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState

    init {
        checkLoginAndLoadPhotos()
    }

    private fun checkLoginAndLoadPhotos() {
        viewModelScope.launch {
            _isLoggedIn.value = checkLoginStatus()
            if (_isLoggedIn.value) {
                loadUserPhotos()
            }
        }
    }

    private fun loadUserPhotos() {
        viewModelScope.launch {
            getUserPhotosUseCase()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .collect { photos ->
                    _uiState.value = GalleryUiState(photos = photos, isLoading = false)
                }
        }
    }

    fun onBookmarkClick(photoId: String) {
        viewModelScope.launch {
            toggleBookmarkUseCase(photoId)
            loadUserPhotos() // 북마크 상태 업데이트 후 새로고침
        }
    }

    private suspend fun checkLoginStatus(): Boolean {
        // TODO: 실제 로그인 여부 확인 로직 적용
        return true // 임시: 항상 로그인 상태
    }
}