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
    /**
     * Chỉ hiển thị cảnh báo lỗi SAU KHI người dùng đã bấm nút Đăng nhập ít nhất 1 lần.
     * Mặc định false → không hiện lỗi đỏ khi vừa mở app.
     */
    val hasAttemptedLogin: Boolean = false,
)
