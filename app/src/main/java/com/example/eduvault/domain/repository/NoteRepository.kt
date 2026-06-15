package com.example.eduvault.domain.repository

import com.example.eduvault.domain.model.UserNote

/**
 * Interface cho NoteRepository — Quản lý ghi chú cá nhân của người dùng.
 *
 * Mỗi ghi chú được gắn với một tài liệu (docId) và lưu riêng per-user trên Firestore.
 * Firestore path: notes/{userId}/documents/{docId}
 */
interface NoteRepository {

    /**
     * Lấy ghi chú của người dùng hiện tại cho tài liệu [docId].
     * Trả về null nếu chưa có ghi chú.
     */
    suspend fun getNote(docId: String): Result<UserNote?>

    /**
     * Lưu hoặc cập nhật ghi chú cho tài liệu.
     */
    suspend fun saveNote(note: UserNote): Result<Unit>

    /**
     * Xóa ghi chú của người dùng cho tài liệu [docId].
     */
    suspend fun deleteNote(docId: String): Result<Unit>

    /**
     * Lấy tất cả ghi chú của người dùng hiện tại,
     * sắp xếp theo thời gian cập nhật gần nhất.
     */
    suspend fun getAllNotes(): Result<List<UserNote>>
}
