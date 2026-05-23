package com.example.eduvault.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel cho màn hình Đặt lại mật khẩu.
 *
 * Quy tắc tuân thủ:
 * - Không có Context, Activity, Fragment, hoặc View nào trong class này.
 * - Trạng thái UI được quản lý 100% qua StateFlow.
 * - Mọi tác vụ bất đồng bộ chạy trong viewModelScope.
 * - TODO: Inject AuthRepository khi hoàn thiện tầng data.
 */
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    // TODO: private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    // ─── Event handlers ───────────────────────────────────────────────────────

    fun onNewPasswordChange(password: String) {
        _uiState.update {
            it.copy(newPassword = password, newPasswordError = null, errorMessage = null)
        }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update {
            it.copy(confirmPassword = password, confirmPasswordError = null, errorMessage = null)
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onResetClick() {
        _uiState.update { it.copy(hasAttemptedReset = true) }
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // TODO: Gọi authRepository.resetPassword(newPassword) khi có data layer
                delay(1500)
                _uiState.update { it.copy(isLoading = false, isResetSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Đặt lại mật khẩu thất bại. Vui lòng thử lại."
                    )
                }
            }
        }
    }

    fun onDismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private fun validateInputs(): Boolean {
        val state = _uiState.value
        var isValid = true

        val newPasswordError = when {
            state.newPassword.isBlank() -> "Vui lòng nhập mật khẩu mới"
            state.newPassword.length < 8 -> "Mật khẩu phải có ít nhất 8 ký tự"
            else -> null
        }

        val confirmPasswordError = when {
            state.confirmPassword.isBlank() -> "Vui lòng xác nhận mật khẩu"
            state.confirmPassword != state.newPassword -> "Mật khẩu xác nhận không khớp"
            else -> null
        }

        if (newPasswordError != null || confirmPasswordError != null) {
            _uiState.update {
                it.copy(
                    newPasswordError = newPasswordError,
                    confirmPasswordError = confirmPasswordError,
                )
            }
            isValid = false
        }

        return isValid
    }
}
