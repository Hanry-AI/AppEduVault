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

import com.example.eduvault.domain.repository.AuthRepository

/**
 * ViewModel cho màn hình đăng ký.
 *
 * Quy tắc tuân thủ:
 * - Không có Context, Activity, Fragment, hoặc View nào trong class này.
 * - Trạng thái UI được quản lý 100% qua StateFlow.
 * - Mọi tác vụ bất đồng bộ chạy trong viewModelScope.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // ─── Event handlers ───────────────────────────────────────────────────────

    fun onFullNameChange(fullName: String) {
        _uiState.update {
            it.copy(
                fullName = fullName,
                fullNameError = null,
                registerError = null
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                registerError = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                // Tự validate lại confirmPassword nếu đã nhập để UX mượt hơn
                confirmPasswordError = if (it.confirmPassword.isNotBlank() && it.hasAttemptedRegister) {
                    if (it.confirmPassword != password) "Mật khẩu xác nhận không khớp" else null
                } else null,
                registerError = null
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = null,
                registerError = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onRegisterClick() {
        // Đánh dấu đã bấm → từ đây mới cho phép hiển thị lỗi đỏ
        _uiState.update { it.copy(hasAttemptedRegister = true) }

        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, registerError = null) }
            val currentState = _uiState.value
            authRepository.register(
                fullName = currentState.fullName,
                email = currentState.email,
                password = currentState.password
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, isRegisterSuccess = true) }
            }
            .onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        registerError = exception.message ?: "Đã xảy ra lỗi không xác định"
                    )
                }
            }
        }
    }

    fun onDismissError() {
        _uiState.update { it.copy(registerError = null) }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private fun validateInputs(): Boolean {
        val s = _uiState.value

        val fullNameError = when {
            s.fullName.isBlank() -> "Vui lòng nhập họ và tên"
            s.fullName.trim().length < 2 -> "Họ và tên phải có ít nhất 2 ký tự"
            else -> null
        }

        val emailError = when {
            s.email.isBlank() -> "Vui lòng nhập email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches() ->
                "Định dạng email không hợp lệ"
            else -> null
        }

        val passwordError = when {
            s.password.isBlank() -> "Vui lòng nhập mật khẩu"
            s.password.length < 8 -> "Mật khẩu phải có ít nhất 8 ký tự"
            else -> null
        }

        val confirmPasswordError = when {
            s.confirmPassword.isBlank() -> "Vui lòng xác nhận mật khẩu"
            s.confirmPassword != s.password -> "Mật khẩu xác nhận không khớp"
            else -> null
        }

        val hasError = listOf(fullNameError, emailError, passwordError, confirmPasswordError)
            .any { it != null }

        if (hasError) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                )
            }
            return false
        }
        return true
    }
}
