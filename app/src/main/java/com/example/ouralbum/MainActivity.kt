package com.example.ouralbum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.ouralbum.presentation.screen.main.MainScreen
import com.example.ouralbum.ui.theme.OurAlbumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OurAlbumTheme {
                val navController = rememberNavController()
                MainScreen(navController = navController)
            }
        }
    }
}
