package com.example.ouralbum.presentation.screen.login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.navigation.NavController
import com.example.ouralbum.presentation.navigation.NavigationItem
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (LoginViewModel.UserInfo) -> Unit // 성공 시 상태 업데이트를 위한 콜백
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1066547476680-q7rf2iqau311lgvvtk8v3gcpihqueb2s.apps.googleusercontent.com") // Web Client ID
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.signInWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            Log.e("LoginScreen", "Google sign-in failed", e)
            Toast.makeText(context, "Google 로그인 실패", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginViewModel.LoginState.Success -> {
                val userInfo = (loginState as LoginViewModel.LoginState.Success).userInfo
                Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                // Firebase 토큰 전파를 위한 대기 시간 (200~500ms)
                delay(500)

                onLoginSuccess(userInfo) // isLoggedIn = true 로 반영, 사용자 정보 전달
                navController.navigate(NavigationItem.MyPage.route) {
                    popUpTo("login") { inclusive = true }
                }
                viewModel.resetState()
            }

            is LoginViewModel.LoginState.Failure -> {
                val message = (loginState as LoginViewModel.LoginState.Failure).message
                Toast.makeText(context, message ?: "로그인 실패", Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }
    }

    // UI
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            launcher.launch(googleSignInClient.signInIntent)
        }) {
            Text("Google로 로그인")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (loginState) {
            is LoginViewModel.LoginState.Loading -> Text("로그인 중...")
            is LoginViewModel.LoginState.Failure -> Text(
                text = (loginState as LoginViewModel.LoginState.Failure).message ?: "로그인 실패",
                color = MaterialTheme.colorScheme.error
            )
            else -> {}
        }
    }
}
