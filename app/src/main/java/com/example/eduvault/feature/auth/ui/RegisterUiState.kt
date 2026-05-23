package com.example.eduvault.feature.auth.ui

/**
 * Trạng thái UI của màn hình đăng ký.
 * Bất biến (immutable), cập nhật qua copy() trong ViewModel.
 */
data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val isLoading: Boolean = false,

    // Lỗi từng field
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    // Lỗi chung từ server/network
    val registerError: String? = null,

    // Chỉ hiện lỗi đỏ SAU KHI người dùng đã bấm nút ít nhất 1 lần
    val hasAttemptedRegister: Boolean = false,

    val isRegisterSuccess: Boolean = false,
)
