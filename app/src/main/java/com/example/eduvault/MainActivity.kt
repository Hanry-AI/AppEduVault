package com.example.eduvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.eduvault.core.navigation.AppNavGraph
import com.example.eduvault.core.navigation.AppRoutes
import com.example.eduvault.core.theme.EduVaultTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = if (firebaseAuth.currentUser != null) {
            AppRoutes.HOME
        } else {
            AppRoutes.LOGIN
        }

        setContent {
            EduVaultTheme {
                // NavGraph xử lý toàn bộ điều hướng giữa các màn hình
                AppNavGraph(startDestination = startDestination)
            }
        }
    }
}
