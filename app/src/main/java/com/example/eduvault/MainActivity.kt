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

        setContent {
            EduVaultTheme {
                // Luôn bắt đầu tại HOME — Guest Mode được xử lý trong ViewModel
                AppNavGraph(startDestination = AppRoutes.HOME)
            }
        }
    }
}
