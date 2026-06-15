package com.example.eduvault.data.repository

import com.example.eduvault.domain.model.UserNote
import com.example.eduvault.domain.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Triển khai NoteRepository dùng Firestore.
 *
 * Cấu trúc Firestore:
 *   notes/{userId}/documents/{docId}
 *     - docTitle: String
 *     - docCourseCode: String
 *     - noteContent: String
 *     - updatedAt: Long (epoch millis)
 *     - createdAt: Long (epoch millis)
 *
 * Quy tắc bảo mật: Firebase Security Rules phải đảm bảo
 *   `allow read, write: if request.auth.uid == userId`
 */
@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : NoteRepository {

    private fun notesCollection(userId: String) =
        firestore.collection("notes").document(userId).collection("documents")

    private fun requireUserId(): String =
        firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("Người dùng chưa đăng nhập.")

    override suspend fun getNote(docId: String): Result<UserNote?> {
        return try {
            val userId = requireUserId()
            val snap = notesCollection(userId).document(docId).get().await()
            if (!snap.exists()) return Result.success(null)

            val note = UserNote(
                docId = docId,
                docTitle = snap.getString("docTitle") ?: "",
                docCourseCode = snap.getString("docCourseCode") ?: "",
                noteContent = snap.getString("noteContent") ?: "",
                updatedAt = snap.getLong("updatedAt") ?: 0L,
                createdAt = snap.getLong("createdAt") ?: 0L
            )
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(Exception("Không thể tải ghi chú: ${e.localizedMessage}"))
        }
    }

    override suspend fun saveNote(note: UserNote): Result<Unit> {
        return try {
            val userId = requireUserId()
            val now = System.currentTimeMillis()

            // Kiểm tra xem ghi chú đã tồn tại chưa để giữ createdAt gốc
            val existing = notesCollection(userId).document(note.docId).get().await()
            val createdAt = if (existing.exists()) existing.getLong("createdAt") ?: now else now

            val data = hashMapOf(
                "docTitle" to note.docTitle,
                "docCourseCode" to note.docCourseCode,
                "noteContent" to note.noteContent,
                "updatedAt" to now,
                "createdAt" to createdAt
            )
            notesCollection(userId).document(note.docId)
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Không thể lưu ghi chú: ${e.localizedMessage}"))
        }
    }

    override suspend fun deleteNote(docId: String): Result<Unit> {
        return try {
            val userId = requireUserId()
            notesCollection(userId).document(docId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Không thể xóa ghi chú: ${e.localizedMessage}"))
        }
    }

    override suspend fun getAllNotes(): Result<List<UserNote>> {
        return try {
            val userId = requireUserId()
            val snapshot = notesCollection(userId).get().await()
            val notes = snapshot.documents
                .mapNotNull { doc ->
                    val content = doc.getString("noteContent") ?: ""
                    // Bỏ qua ghi chú trống
                    if (content.isBlank()) return@mapNotNull null
                    UserNote(
                        docId = doc.id,
                        docTitle = doc.getString("docTitle") ?: "",
                        docCourseCode = doc.getString("docCourseCode") ?: "",
                        noteContent = content,
                        updatedAt = doc.getLong("updatedAt") ?: 0L,
                        createdAt = doc.getLong("createdAt") ?: 0L
                    )
                }
                .sortedByDescending { it.updatedAt }
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(Exception("Không thể tải danh sách ghi chú: ${e.localizedMessage}"))
        }
    }
}
