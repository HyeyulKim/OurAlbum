package com.example.ouralbum.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ouralbum.MainActivity
import com.example.ouralbum.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@AndroidEntryPoint // Hilt 사용 중이면 추가
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. 시스템 SplashScreen API 초기화
        val splash = installSplashScreen()

        // 2. 시스템 SplashScreen 제거
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splash.setOnExitAnimationListener { it.remove() }
        }

        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen()
        }

        // 일정 시간 후 MainActivity로 전환
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500) // 1.5초 후 전환
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF248350)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "Splash Logo",
            modifier = Modifier.size(240.dp)
        )
    }
}
