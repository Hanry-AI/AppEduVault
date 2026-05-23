package com.example.eduvault.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eduvault.feature.auth.ui.ForgotPasswordScreen
import com.example.eduvault.feature.auth.ui.LoginScreen
import com.example.eduvault.feature.auth.ui.RegisterScreen
import com.example.eduvault.feature.auth.ui.ResetPasswordScreen
import com.example.eduvault.feature.home.ui.HomeScreen

/**
 * Nav graph chính của app.
 * Tất cả điều hướng đều đi qua đây — không navigate trực tiếp trong Screen/ViewModel.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppRoutes.LOGIN,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {

        // ── Đăng nhập ──────────────────────────────────────────────────────
        composable(route = AppRoutes.LOGIN) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AppRoutes.FORGOT_PASSWORD)
                },
            )
        }

        // ── Đăng ký ────────────────────────────────────────────────────────
        composable(route = AppRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        // ── Quên mật khẩu ──────────────────────────────────────────────────
        composable(route = AppRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToResetPassword = {
                    navController.navigate(AppRoutes.RESET_PASSWORD) {
                        // Xóa màn hình ForgotPassword khỏi back stack khi sang ResetPassword
                        popUpTo(AppRoutes.FORGOT_PASSWORD) { inclusive = true }
                    }
                },
            )
        }

        // ── Đặt lại mật khẩu ───────────────────────────────────────────────
        composable(route = AppRoutes.RESET_PASSWORD) {
            ResetPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetSuccess = {
                    // Sau khi đặt lại mật khẩu thành công → về Login, xóa toàn bộ auth stack
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = false }
                    }
                },
            )
        }

        // ── Home ────────────────────────────────────────────────────────────
        composable(route = AppRoutes.HOME) {
            HomeScreen()
        }
    }
}
