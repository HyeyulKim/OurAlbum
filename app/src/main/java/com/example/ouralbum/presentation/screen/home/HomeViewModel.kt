package com.example.ouralbum.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.usecase.GetPhotosUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            getPhotosUseCase()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .collect { photos ->
                    _uiState.value = HomeUiState(photos = photos, isLoading = false)
                }
        }
    }

    fun onBookmarkClick(photoId: String) {
        viewModelScope.launch {
            toggleBookmarkUseCase(photoId)
            loadPhotos() // 북마크 상태 업데이트 후 새로고침
        }
    }
}
