package com.example.eduvault.data.repository

import com.example.eduvault.domain.repository.DocumentRepository
import com.example.eduvault.feature.library.ui.LibraryDoc
import com.example.eduvault.feature.library.ui.LibraryDocType
import com.example.eduvault.domain.repository.NotificationRepository
import com.example.eduvault.domain.model.NotificationType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.eduvault.core.mock.MockDataProvider
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val notificationRepository: NotificationRepository
) : DocumentRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedMockDocumentsIfNotExist()
        }
    }

    override suspend fun uploadUserDocument(
        title: String,
        subjectCode: String,
        docType: String,
        fileUri: String
    ): Result<Unit> {
        val firebaseUser = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Người dùng chưa đăng nhập."))
        return try {
            // Lấy thông tin user hiện tại từ Firestore
            val userSnap = firestore.collection("users").document(firebaseUser.uid).get().await()
            val school = userSnap.getString("university") ?: ""
            val currentUploadCount = userSnap.getLong("uploadCount") ?: 0L
            val currentCredits = userSnap.getLong("documentCredits") ?: 1L

            // Chạy kiểm duyệt AI tức thì qua từ khóa Heuristic
            val dangerKeywords = listOf(
                "18+", "casino", "baccarat", "quảng cáo", "mua bán", 
                "gian lận", "hack", "crack", "spam", "cờ bạc", "sex"
            )
            val warningKeywords = listOf(
                "nhạy cảm", "tài liệu mật", "leak", "lộ đề", "bán tài liệu", "tục"
            )

            val hasDanger = dangerKeywords.any { title.contains(it, ignoreCase = true) }
            val hasWarning = warningKeywords.any { title.contains(it, ignoreCase = true) }

            val (status, checkResult) = when {
                hasDanger -> "PENDING" to "DANGER"
                hasWarning -> "PENDING" to "WARNING"
                else -> "APPROVED" to "SAFE"
            }

            // Tải file lên Firebase Storage bằng Coroutines
            var downloadUrl = ""
            var fileName = ""
            var fileSizeString = ""
            var detectedContentType = ""
            
            if (fileUri.isNotEmpty()) {
                val localFile = java.io.File(fileUri)
                if (localFile.exists()) {
                    fileName = localFile.name
                    val fileSizeInBytes = localFile.length()
                    fileSizeString = if (fileSizeInBytes >= 1024 * 1024) {
                        String.format(java.util.Locale.US, "%.2f MB", fileSizeInBytes / (1024.0 * 1024.0))
                    } else {
                        String.format(java.util.Locale.US, "%.2f KB", fileSizeInBytes / 1024.0)
                    }
                    
                    val extension = localFile.extension.lowercase()
                    detectedContentType = when (extension) {
                        "pdf" -> "application/pdf"
                        "doc" -> "application/msword"
                        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        "ppt" -> "application/vnd.ms-powerpoint"
                        "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                        "png" -> "image/png"
                        "jpg", "jpeg" -> "image/jpeg"
                        else -> "application/octet-stream"
                    }
                    
                    val uuid = java.util.UUID.randomUUID().toString()
                    val storagePath = "documents/${firebaseUser.uid}/${uuid}_$fileName"
                    val storageRef = storage.reference.child(storagePath)
                    
                    val metadata = com.google.firebase.storage.StorageMetadata.Builder()
                        .setContentType(detectedContentType)
                        .build()
                        
                    storageRef.putFile(android.net.Uri.fromFile(localFile), metadata).await()
                    downloadUrl = storageRef.downloadUrl.await().toString()
                }
            }

            // Tạo tài liệu mới trong Firestore collection "documents"
            val docId = firestore.collection("documents").document().id
            val docMap = hashMapOf(
                "documentID" to docId,
                "courseCode" to subjectCode.trim().uppercase(),
                "title" to title.trim(),
                "type" to docType,
                "quantityLabel" to if (fileSizeString.isNotEmpty()) fileSizeString else "Tài liệu tự đăng",
                "rating" to 5.0f,
                "views" to 0L,
                "school" to school,
                "authorId" to firebaseUser.uid,
                "downloadUrl" to downloadUrl,
                "fileName" to fileName,
                "fileSize" to fileSizeString,
                "contentType" to detectedContentType,
                "uploadedAt" to com.google.firebase.Timestamp(java.util.Date()),
                "createdAt" to com.google.firebase.Timestamp(java.util.Date()),
                "aiVerified" to true,
                "aiCheckResult" to checkResult,
                "status" to status,
                "reportCount" to 0L
            )
            firestore.collection("documents").document(docId).set(docMap).await()

            // Cập nhật stats user (+1 upload, +1 credit)
            firestore.collection("users").document(firebaseUser.uid).update(
                mapOf(
                    "uploadCount" to currentUploadCount + 1,
                    "documentCredits" to currentCredits + 1
                )
            ).await()

            // Sinh thông báo sự kiện upload tài liệu thành công thực tế lưu vào Firestore
            notificationRepository.addNotification(
                userId = firebaseUser.uid,
                title = "📚 Đóng góp tài liệu",
                content = "Tài liệu '${title}' của bạn đã được tải lên hệ thống thành công! (+1 Credit học tập 💰)",
                type = NotificationType.SYSTEM
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Đăng tải tài liệu thất bại: ${e.localizedMessage}"))
        }
    }

    override suspend fun getDocuments(): Result<List<LibraryDoc>> {
        return try {
            val snapshot = firestore.collection("documents").get().await()
            val sortedDocs = snapshot.documents.sortedByDescending { doc ->
                val ts = doc.get("uploadedAt") ?: doc.get("createdAt")
                when (ts) {
                    is com.google.firebase.Timestamp -> ts.toDate().time
                    is Long -> ts
                    is Number -> ts.toLong()
                    else -> 0L
                }
            }
            val list = sortedDocs.mapNotNull { doc ->
                val status = doc.getString("status") ?: "APPROVED"
                // Chỉ hiển thị tài liệu đã được duyệt phê duyệt cho người dùng thường
                if (status != "APPROVED") return@mapNotNull null

                val title = doc.getString("title") ?: ""
                if (title.isBlank()) return@mapNotNull null
                val id = doc.getString("documentID") ?: doc.id
                val courseCode = doc.getString("courseCode") ?: ""
                val typeStr = doc.getString("type") ?: "Ghi chú"
                
                val type = when (typeStr.lowercase()) {
                    "quiz", "bộ đề", "trắc nghiệm" -> LibraryDocType.QUIZ
                    "slide", "bài giảng" -> LibraryDocType.SLIDE
                    "tóm tắt", "summary" -> LibraryDocType.SUMMARY
                    else -> LibraryDocType.NOTE
                }
                
                val quantityLabel = doc.getString("quantityLabel") ?: "Tài liệu tự đăng"
                val rating = doc.getDouble("rating")?.toFloat() ?: 5.0f
                
                val rawViews = doc.get("views")
                val views = when (rawViews) {
                    is Number -> rawViews.toInt().toString()
                    is String -> rawViews
                    else -> "0"
                }

                val aiVerified = doc.getBoolean("aiVerified") ?: false
                val aiCheckResult = doc.getString("aiCheckResult") ?: ""
                val reportCount = doc.getLong("reportCount")?.toInt() ?: 0

                LibraryDoc(
                    id = id,
                    courseCode = courseCode,
                    title = title,
                    type = type,
                    quantityLabel = quantityLabel,
                    rating = rating,
                    views = views,
                    bgIndex = Math.abs(title.hashCode() % 6),
                    aiVerified = aiVerified,
                    aiCheckResult = aiCheckResult,
                    status = status,
                    reportCount = reportCount
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception("Không thể tải danh sách tài liệu: ${e.localizedMessage}"))
        }
    }

    override suspend fun reportDocument(docId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("documents").document(docId)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                return Result.failure(Exception("Tài liệu không tồn tại."))
            }
            docRef.update("reportCount", FieldValue.increment(1)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Tố cáo tài liệu thất bại: ${e.localizedMessage}"))
        }
    }

    override suspend fun getCachedDocViewerContent(docId: String): Result<com.example.eduvault.domain.model.DocViewerTabsContent?> {
        return try {
            val snap = firestore.collection("documents").document(docId).get().await()
            if (!snap.exists()) return Result.success(null)

            val version = snap.getLong("aiContentVersion") ?: 0L
            if (version != 1L) return Result.success(null)

            val content = snap.getString("aiContent") ?: ""
            val keyPoints = snap.getString("aiKeyPoints") ?: ""
            val practice = snap.getString("aiPractice") ?: ""

            if (content.isBlank() || keyPoints.isBlank() || practice.isBlank()) {
                Result.success(null)
            } else {
                Result.success(
                    com.example.eduvault.domain.model.DocViewerTabsContent(content, keyPoints, practice)
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveDocViewerContent(
        docId: String,
        content: com.example.eduvault.domain.model.DocViewerTabsContent
    ): Result<Unit> {
        return try {
            firestore.collection("documents").document(docId).set(
                mapOf(
                    "aiContent" to content.content,
                    "aiKeyPoints" to content.keyPoints,
                    "aiPractice" to content.practice,
                    "aiContentVersion" to 1L
                ),
                com.google.firebase.firestore.SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedMockDocumentsIfNotExist() {
        try {
            // Kiểm tra xem tài liệu mock thứ nhất (id = "1") đã có trên Firestore chưa
            val checkSnap = firestore.collection("documents").document("1").get().await()
            if (!checkSnap.exists()) {
                // Tự động seed cả 9 tài liệu mock lên Firestore
                for (mockDoc in MockDataProvider.allMockDocs) {
                    val docRef = firestore.collection("documents").document(mockDoc.id)
                    val docMap = hashMapOf(
                        "documentID" to mockDoc.id,
                        "courseCode" to mockDoc.courseCode,
                        "title" to mockDoc.title,
                        "type" to when (mockDoc.type) {
                            LibraryDocType.QUIZ -> "Quiz"
                            LibraryDocType.SLIDE -> "Slide"
                            LibraryDocType.SUMMARY -> "Tóm tắt"
                            else -> "Ghi chú"
                        },
                        "quantityLabel" to mockDoc.quantityLabel,
                        "rating" to mockDoc.rating,
                        "views" to (mockDoc.views.replace("k", "000").replace(".", "").toLongOrNull() ?: 0L),
                        "school" to "Đại học Công nghệ thông tin",
                        "authorId" to "system_mock",
                        "downloadUrl" to "",
                        "fileName" to "",
                        "fileSize" to "",
                        "contentType" to "",
                        "uploadedAt" to com.google.firebase.Timestamp(java.util.Date()),
                        "createdAt" to com.google.firebase.Timestamp(java.util.Date()),
                        "aiVerified" to true,
                        "aiCheckResult" to "SAFE",
                        "status" to "APPROVED",
                        "reportCount" to 0L
                    )
                    docRef.set(docMap).await()
                }
                android.util.Log.d("DocumentRepository", "Đã seed thành công 9 tài liệu mock lên Firestore.")
            }
        } catch (e: Exception) {
            android.util.Log.e("DocumentRepository", "Lỗi seed tài liệu mock lên Firestore: ${e.localizedMessage}")
        }
    }
}
