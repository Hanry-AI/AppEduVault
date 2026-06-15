package com.example.eduvault.domain.repository

import com.example.eduvault.domain.model.DocViewerTabsContent
import com.example.eduvault.feature.library.ui.LibraryDoc

/**
 * Interface cho DocumentRepository - Quản lý tài liệu.
 */
interface DocumentRepository {
    
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
    suspend fun getDocuments(): Result<List<LibraryDoc>>

    /**
     * Tố cáo tài liệu vi phạm.
     */
    suspend fun reportDocument(docId: String): Result<Unit>

    /**
     * Lấy nội dung chi tiết DocViewer đã lưu cache trong Firestore (nếu có).
     */
    suspend fun getCachedDocViewerContent(docId: String): Result<DocViewerTabsContent?>

    /**
     * Lưu cache nội dung DocViewer vào Firestore.
     */
    suspend fun saveDocViewerContent(
        docId: String, 
        content: DocViewerTabsContent
    ): Result<Unit>
}
