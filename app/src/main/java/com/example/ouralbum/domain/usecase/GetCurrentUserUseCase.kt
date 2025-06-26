package com.example.ouralbum.domain.usecase

import com.example.ouralbum.domain.model.User
import com.example.ouralbum.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User? {
        return userRepository.getCurrentUser()
    }
}
