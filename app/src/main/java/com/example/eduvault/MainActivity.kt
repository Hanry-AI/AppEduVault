package com.example.eduvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.eduvault.core.theme.EduVaultTheme
import com.example.eduvault.feature.auth.ui.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduVaultTheme {
                // TODO: Thay bằng NavHost khi có navigation graph hoàn chỉnh
                LoginScreen(
                    onNavigateToHome = {
                        // TODO: Navigate to HomeScreen
                    },
                    onNavigateToRegister = {
                        // TODO: Navigate to RegisterScreen
                    }
                )
            }
        }
    }
}
