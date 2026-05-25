package com.example.eduvault.domain.repository

import com.example.eduvault.domain.model.User

/**
 * Interface của AuthRepository — định nghĩa hợp đồng cho tầng data.
 *
 * Quy tắc tuân thủ:
 * - Interface này không biết gì về Firebase, Retrofit, hay bất kỳ thư viện bên ngoài nào.
 * - Mọi phương thức trả về Result<T> để xử lý lỗi an toàn kiểu (type-safe).
 * - ViewModel chỉ gọi interface này, không quan tâm đến implementation.
 */
interface AuthRepository {

    /**
     * Đăng nhập bằng email/password.
     * @return [Result.success] với [User] nếu thành công, [Result.failure] nếu thất bại.
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Đăng ký tài khoản mới.
     * Tạo user trong Firebase Auth VÀ lưu thông tin vào Firestore.
     * @return [Result.success] với [User] nếu thành công, [Result.failure] nếu thất bại.
     */
    suspend fun register(fullName: String, email: String, password: String): Result<User>

    /**
     * Gửi email đặt lại mật khẩu.
     * @return [Result.success] nếu email được gửi, [Result.failure] nếu email không tồn tại.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Lấy thông tin người dùng hiện tại từ Firestore (nếu đã đăng nhập).
     * @return [Result.success] với [User] hoặc null nếu chưa đăng nhập.
     */
    suspend fun getCurrentUser(): Result<User?>

    /**
     * Cập nhật thông tin hồ sơ của người dùng hiện tại.
     */
    suspend fun updateProfile(fullName: String, university: String, avatarUrl: String): Result<User>

    /**
     * Tăng lượt sử dụng tài liệu khả dụng (+ amount).
     */
    suspend fun addCredits(amount: Int): Result<User>

    /**
     * Giảm 1 lượt sử dụng tài liệu khả dụng (-1).
     */
    suspend fun consumeCredit(): Result<User>

    /**
     * Đăng tải tài liệu mới của người dùng lên hệ thống.
     */
    suspend fun uploadUserDocument(
        title: String,
        subjectCode: String,
        docType: String,
        fileUri: String
    ): Result<Unit>

    /**
     * Lấy danh sách tài liệu từ Firestore.
     */
    suspend fun getDocuments(): Result<List<com.example.eduvault.feature.library.ui.LibraryDoc>>

    /**
     * Đăng xuất.
     */
    fun logout()
}

