package com.example.eduvault.feature.auth.ui

/**
 * Trạng thái UI của màn hình đặt lại mật khẩu.
 * Người dùng đến đây SAU KHI xác thực OTP thành công từ ForgotPasswordScreen.
 */
data class ResetPasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isResetSuccess: Boolean = false,
    /**
     * Chỉ hiển thị lỗi SAU KHI người dùng đã bấm nút ít nhất 1 lần.
     */
    val hasAttemptedReset: Boolean = false,

    // ─── Luồng Firebase Thật / Demo ──────────────────────────────────────────
    val isDemoMode: Boolean = true,
    val oobCode: String? = null,
)
