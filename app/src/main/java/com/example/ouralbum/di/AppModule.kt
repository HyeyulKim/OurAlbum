package com.example.ouralbum.di

import com.example.ouralbum.data.remote.PhotoUploader
import com.example.ouralbum.data.repository.PhotoRepositoryImpl
import com.example.ouralbum.data.repository.UserRepositoryImpl
import com.example.ouralbum.domain.repository.PhotoRepository
import com.example.ouralbum.domain.repository.UserRepository
import com.example.ouralbum.domain.usecase.GetBookmarkedPhotosUseCase
import com.example.ouralbum.domain.usecase.GetCurrentUserUseCase
import com.example.ouralbum.domain.usecase.GetPhotosUseCase
import com.example.ouralbum.domain.usecase.GetUserPhotosUseCase
import com.example.ouralbum.domain.usecase.LogoutUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Firebase 인스턴스들 제공
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    // PhotoUploader
    @Provides
    @Singleton
    fun providePhotoUploader(
        storage: FirebaseStorage,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): PhotoUploader = PhotoUploader(storage, firestore, auth)

    // PhotoRepository
    @Provides
    @Singleton
    fun providePhotoRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        storage: FirebaseStorage
    ): PhotoRepository = PhotoRepositoryImpl(firestore, firebaseAuth, storage)

    // 전체 사진
    @Provides
    fun provideGetPhotosUseCase(repository: PhotoRepository): GetPhotosUseCase =
        GetPhotosUseCase(repository)

    // 내 사진(현재 유저)
    @Provides
    fun provideGetUserPhotosUseCase(
        repository: PhotoRepository
    ): GetUserPhotosUseCase = GetUserPhotosUseCase(repository)

    @Provides
    fun provideGetBookmarkedPhotosUseCase(
        repository: PhotoRepository
    ): GetBookmarkedPhotosUseCase = GetBookmarkedPhotosUseCase(repository)

    @Provides
    fun provideToggleBookmarkUseCase(repository: PhotoRepository): ToggleBookmarkUseCase =
        ToggleBookmarkUseCase(repository)

    // User 관련
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
