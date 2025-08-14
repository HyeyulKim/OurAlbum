package com.example.ouralbum.di

import com.example.ouralbum.data.auth.FirebaseAuthStateProvider
import com.example.ouralbum.domain.auth.AuthStateProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindModule {
    @Binds
    @Singleton
    abstract fun bindAuthStateProvider(
        impl: FirebaseAuthStateProvider
    ): AuthStateProvider
}