package com.example.eduvault.domain.model

/**
 * Phân loại thông báo hệ thống EduVault.
 */
enum class NotificationType {
    ACHIEVEMENT,
    AI,
    COMMUNITY,
    SYSTEM
}

/**
 * Model dữ liệu đại diện cho một thông báo động của người dùng.
 */
data class EduNotification(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val userId: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val isRead: Boolean = false
) {
    /**
     * Hàm helper tự động tính khoảng thời gian tương đối so với hiện tại để hiển thị UX đẹp mắt.
     */
    fun getFormattedTime(): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 0 -> "Vừa xong"
            diff < 60_000 -> "Vừa xong"
            diff < 3600_000 -> "${diff / 60_000} phút trước"
            diff < 86400_000 -> "${diff / 3600_000} giờ trước"
            diff < 86400_000 * 7 -> "${diff / 86400_000} ngày trước"
            else -> "Vài ngày trước"
        }
    }
}
