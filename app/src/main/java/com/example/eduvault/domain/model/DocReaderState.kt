package com.example.eduvault.domain.model

/**
 * Sealed class đại diện cho trạng thái của người đọc tài liệu.
 *
 * Ba trạng thái khóa tài liệu (chapter 2, 3, 4):
 *  - GuestLocked: Chưa đăng nhập → hiển thị overlay "Đăng nhập để đọc full"
 *  - NoCredits: Đã đăng nhập nhưng hết Credit → "Tải tài liệu lên để nhận Credit"
 *  - HasCredits: Đã đăng nhập và có Credit → "Dùng 1 Credit để mở khóa"
 *  - Unlocked: Đã trả Credit, tài liệu được mở toàn bộ
 */
sealed class DocReaderState {
    /** Chưa đăng nhập */
    object GuestLocked : DocReaderState()

    /** Đã đăng nhập nhưng credit == 0 */
    object NoCredits : DocReaderState()

    /** Đã đăng nhập và có credit ≥ 1 */
    data class HasCredits(val count: Int) : DocReaderState()

    /** Đã trả credit, full access */
    object Unlocked : DocReaderState()
}
