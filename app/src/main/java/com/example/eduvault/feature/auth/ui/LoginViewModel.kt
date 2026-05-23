package com.example.eduvault.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel cho màn hình đăng nhập.
 *
 * Quy tắc tuân thủ:
 * - Không có Context, Activity, Fragment, hoặc View nào trong class này.
 * - Trạng thái UI được quản lý 100% qua StateFlow.
 * - Mọi tác vụ bất đồng bộ chạy trong viewModelScope.
 * - TODO: Inject AuthRepository khi hoàn thiện tầng data.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    // TODO: private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // ─── Event handlers (gọi từ UI khi người dùng tương tác) ─────────────────

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, loginError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, loginError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClick() {
        // Đánh dấu đã có ít nhất 1 lần bấm đăng nhập → từ đây mới cho phép hiển thị lỗi đỏ
        _uiState.update { it.copy(hasAttemptedLogin = true) }

        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loginError = null) }
            try {
                // TODO: Gọi authRepository.login(email, password) khi có data layer
                // Tạm thời simulate delay để demo UX loading
                kotlinx.coroutines.delay(1500)
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginError = e.message ?: "Đã xảy ra lỗi không xác định"
                    )
                }
            }
        }
    }

    fun onDismissError() {
        _uiState.update { it.copy(loginError = null) }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private fun validateInputs(): Boolean {
        val currentState = _uiState.value
        var isValid = true

        val emailError = when {
            currentState.email.isBlank() -> "Vui lòng nhập email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches() ->
                "Định dạng email không hợp lệ"
            else -> null
        }

        val passwordError = when {
            currentState.password.isBlank() -> "Vui lòng nhập mật khẩu"
            currentState.password.length < 8 -> "Mật khẩu phải có ít nhất 8 ký tự"
            else -> null
        }

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            isValid = false
        }

        return isValid
    }
}
