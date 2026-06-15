package com.example.eduvault.domain.model

/**
 * Sealed class đại diện cho trạng thái xác thực của người dùng.
 *
 * Dùng sealed class thay vì nullable User để:
 * - Compiler enforce exhaustive when-check (không thể quên case nào)
 * - Logic Guest/Authenticated tập trung tại 1 nơi, không kiểm tra null rải rác
 */
sealed class AuthState {
    /** Người dùng chưa đăng nhập — chế độ khách */
    object Guest : AuthState()

    /** Người dùng đã đăng nhập thành công */
    data class Authenticated(val user: User) : AuthState()
}
