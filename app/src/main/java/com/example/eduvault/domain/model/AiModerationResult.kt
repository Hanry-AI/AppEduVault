package com.example.eduvault.domain.model

/**
 * Kết quả kiểm duyệt tài liệu bằng AI (Heuristic).
 */
enum class AiModerationResult {
    SAFE,    // 🟢 An toàn
    WARNING, // 🟡 Nghi vấn (Cần xem xét)
    DANGER   // 🔴 Vi phạm (Phát hiện từ khóa cấm)
}
