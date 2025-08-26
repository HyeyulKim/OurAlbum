package com.example.ouralbum.presentation.screen.login

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.navigation.NavController
import com.example.ouralbum.R
import com.example.ouralbum.presentation.navigation.NavigationItem
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (LoginViewModel.UserInfo) -> Unit // 성공 시 상태 업데이트를 위한 콜백
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    val enabled = loginState !is LoginViewModel.LoginState.Loading
    val interaction = remember { MutableInteractionSource() }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            //.background(Color(0xFF248350))
    ) {
        when (loginState) {
            is LoginViewModel.LoginState.Loading -> {
                LaunchedEffect(loginState) {
                    Toast.makeText(context, "로그인 중...", Toast.LENGTH_SHORT).show()
                }
            }

            is LoginViewModel.LoginState.Failure -> {
                val msg = (loginState as LoginViewModel.LoginState.Failure).message ?: "로그인 실패"
                LaunchedEffect(msg) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            else -> {}
        }


        // 상단 로고
        Image(
            painter = painterResource(id = R.drawable.login_logo),
            contentDescription = "Login Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 200.dp)
                .size(240.dp)
        )

        Text(
            text = "우리의 앨범에 로그인해서\n우리의 추억을 기록하세요",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Center)
        )

        Image(
            painter = painterResource(id = R.drawable.google_btn_ctn),
            contentDescription = "Continue with Google",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 150.dp)
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable(
                    enabled = enabled,
                    interactionSource = interaction,
                    indication = rememberRipple(bounded = true, radius = 24.dp)
                ) { launcher.launch(googleSignInClient.signInIntent) }
                .alpha(if (enabled) 1f else 0.6f)
        )

    }
}
