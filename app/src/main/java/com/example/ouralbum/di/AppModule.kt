package com.example.ouralbum.di

import com.example.ouralbum.data.repository.PhotoRepositoryImpl
import com.example.ouralbum.domain.repository.PhotoRepository
import com.example.ouralbum.domain.usecase.GetPhotosUseCase
import com.example.ouralbum.domain.usecase.ToggleBookmarkUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePhotoRepository(): PhotoRepository = PhotoRepositoryImpl()

    @Provides
    fun provideGetPhotosUseCase(repository: PhotoRepository): GetPhotosUseCase =
        GetPhotosUseCase(repository)

    @Provides
    fun provideToggleBookmarkUseCase(repository: PhotoRepository): ToggleBookmarkUseCase =
        ToggleBookmarkUseCase(repository)
}
