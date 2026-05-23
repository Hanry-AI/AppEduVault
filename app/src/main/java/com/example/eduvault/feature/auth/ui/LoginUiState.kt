package com.example.eduvault.feature.auth.ui

/**
 * Trạng thái UI của màn hình đăng nhập.
 * Bất biến (immutable), cập nhật qua copy() trong ViewModel.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val isLoginSuccess: Boolean = false,
)
