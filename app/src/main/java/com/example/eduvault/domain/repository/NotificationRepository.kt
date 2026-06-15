package com.example.eduvault.domain.repository

import com.example.eduvault.domain.model.EduNotification
import com.example.eduvault.domain.model.NotificationType

/**
 * Interface cho NotificationRepository - Quản lý nạp và ghi thông báo động từ database.
 */
interface NotificationRepository {
    
    /**
     * Lấy danh sách thông báo lọc theo userId của người dùng, sắp xếp theo thời gian mới nhất.
     */
    suspend fun getNotifications(userId: String): Result<List<EduNotification>>

    /**
     * Kiểm tra và khởi tạo 4 thông báo mẫu động nếu cơ sở dữ liệu trống đối với user này.
     */
    suspend fun ensureDefaultNotifications(userId: String): Result<Unit>

    /**
     * Thêm một thông báo mới (gắn liền với các sự kiện thực tế như upload tài liệu, tạo quiz, summary).
     */
    suspend fun addNotification(
        userId: String,
        title: String,
        content: String,
        type: NotificationType
    ): Result<Unit>

    /**
     * Đánh dấu thông báo đã đọc.
     */
    suspend fun markAsRead(id: String): Result<Unit>
}
