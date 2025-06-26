package com.example.ouralbum.di

import com.example.ouralbum.data.repository.PhotoRepositoryImpl
import com.example.ouralbum.data.repository.UserRepositoryImpl
import com.example.ouralbum.domain.repository.PhotoRepository
import com.example.ouralbum.domain.repository.UserRepository
import com.example.ouralbum.domain.usecase.GetCurrentUserUseCase
import com.example.ouralbum.domain.usecase.GetPhotosUseCase
import com.example.ouralbum.domain.usecase.LogoutUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Photo 관련
    @Provides
    @Singleton
    fun providePhotoRepository(): PhotoRepository = PhotoRepositoryImpl()

    @Provides
    fun provideGetPhotosUseCase(repository: PhotoRepository): GetPhotosUseCase =
        GetPhotosUseCase(repository)

    @Provides
    fun provideToggleBookmarkUseCase(repository: PhotoRepository): ToggleBookmarkUseCase =
        ToggleBookmarkUseCase(repository)

    // User 관련
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(firebaseAuth: FirebaseAuth): UserRepository =
        UserRepositoryImpl(firebaseAuth)

    @Provides
    fun provideGetCurrentUserUseCase(userRepository: UserRepository): GetCurrentUserUseCase =
        GetCurrentUserUseCase(userRepository)

    @Provides
    fun provideLogoutUseCase(userRepository: UserRepository): LogoutUseCase =
        LogoutUseCase(userRepository)
}
