package com.example.ouralbum.presentation.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.usecase.GetPhotoDetailUseCase
import com.example.ouralbum.domain.usecase.UpdatePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoEditViewModel @Inject constructor(
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val updatePhotoUseCase: UpdatePhotoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val photoId: String = checkNotNull(savedStateHandle["photoId"])
    private val _uiState = MutableStateFlow(PhotoEditUiState())
    val uiState: StateFlow<PhotoEditUiState> = _uiState

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getPhotoDetailUseCase(photoId).collect { detail ->
                if (detail == null) {
                    _uiState.update { it.copy(isLoading = false, error = "게시물을 찾을 수 없습니다.") }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            title = detail.title,
                            content = detail.content,
                            error = null
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(s: String) = _uiState.update { it.copy(title = s) }
    fun onContentChange(s: String) = _uiState.update { it.copy(content = s) }

    fun submit() {
        val (title, content) = _uiState.value.let { it.title to it.content }
        viewModelScope.launch {
            try {
                updatePhotoUseCase(photoId, title, content)
                _uiState.update { it.copy(done = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "수정 실패") }
            }
        }
    }
}
