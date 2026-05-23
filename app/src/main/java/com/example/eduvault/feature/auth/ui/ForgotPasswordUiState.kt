package com.example.eduvault.feature.auth.ui

/**
 * Trạng thái UI của màn hình quên mật khẩu.
 * Luồng: Nhập email → gửi OTP → nhập OTP → xác nhận thành công → sang ResetPassword.
 */
data class ForgotPasswordUiState(
    // ─── Bước 1: Nhập email ─────────────────────────────────────────────────────
    val email: String = "",
    val emailError: String? = null,

    // ─── Bước 2: Xác thực OTP ───────────────────────────────────────────────────
    val otp: String = "",
    val otpError: String? = null,

    // ─── Trạng thái bước hiện tại ───────────────────────────────────────────────
    /** false = đang ở bước nhập email | true = đã gửi OTP, đang chờ xác nhận */
    val isOtpSent: Boolean = false,

    // ─── Loading / Error / Success ──────────────────────────────────────────────
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isVerifySuccess: Boolean = false,

    // ─── Chỉ hiện lỗi sau khi người dùng bấm nút ────────────────────────────────
    val hasAttemptedSend: Boolean = false,
    val hasAttemptedVerify: Boolean = false,

    // ─── Đếm ngược resend OTP (giây) ────────────────────────────────────────────
    val resendCountdown: Int = 0,
)
