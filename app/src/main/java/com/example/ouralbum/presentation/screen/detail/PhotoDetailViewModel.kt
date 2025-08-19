package com.example.ouralbum.presentation.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ouralbum.domain.usecase.DeletePhotoUseCase
import com.example.ouralbum.domain.usecase.GetPhotoDetailUseCase
import com.google.firebase.auth.FirebaseAuth
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
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val photoId: String = checkNotNull(savedStateHandle["photoId"])
    private val _uiState = MutableStateFlow(PhotoDetailUiState())
    val uiState: StateFlow<PhotoDetailUiState> = _uiState

    private var subJob: Job? = null

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
            }
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
}
