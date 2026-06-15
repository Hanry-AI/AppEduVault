package com.example.eduvault.domain.repository

import com.example.eduvault.domain.model.DocumentCategory

/**
 * Interface cho CategoryRepository - Quản lý danh mục động.
 */
interface CategoryRepository {
    
    /**
     * Lấy danh sách danh mục từ Firestore.
     */
    suspend fun getCategories(): Result<List<DocumentCategory>>

    /**
     * Thêm danh mục mới.
     */
    suspend fun addCategory(name: String, emoji: String): Result<Unit>

    /**
     * Cập nhật danh mục.
     */
    suspend fun updateCategory(id: String, name: String, emoji: String): Result<Unit>

    /**
     * Xóa danh mục.
     */
    suspend fun deleteCategory(id: String): Result<Unit>
}
