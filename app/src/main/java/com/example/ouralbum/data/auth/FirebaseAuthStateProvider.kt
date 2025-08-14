package com.example.ouralbum.data.auth

import com.example.ouralbum.domain.auth.AuthStateProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthStateProvider @Inject constructor(
    private val auth: FirebaseAuth
) : AuthStateProvider {
    override val isLoggedIn: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa -> trySend(fa.currentUser != null) }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser != null) // 초기값
        awaitClose { auth.removeAuthStateListener(listener) }
    }
}
