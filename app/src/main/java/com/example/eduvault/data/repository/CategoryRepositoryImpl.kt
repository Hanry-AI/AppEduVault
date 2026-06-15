package com.example.eduvault.data.repository

import com.example.eduvault.domain.model.DocumentCategory
import com.example.eduvault.domain.repository.CategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<DocumentCategory>> {
        return try {
            val snapshot = firestore.collection("categories").get().await()
            if (snapshot.isEmpty) {
                // Tự động chèn danh mục mẫu chỉ một lần duy nhất trong lần khởi tạo đầu tiên để tránh bị ghi đè trùng lặp
                val defaultCategories = listOf(
                    DocumentCategory("cat_01", "Kinh tế học", "📊", 124),
                    DocumentCategory("cat_02", "Marketing", "🧩", 89),
                    DocumentCategory("cat_03", "Kế toán", "📋", 67),
                    DocumentCategory("cat_04", "Pháp luật", "⚖️", 45),
                    DocumentCategory("cat_05", "Thống kê", "📈", 112),
                    DocumentCategory("cat_06", "Tài chính", "💰", 78)
                )
                for (cat in defaultCategories) {
                    val map = hashMapOf(
                        "categoryID" to cat.id,
                        "name" to cat.name,
                        "emoji" to cat.emoji,
                        "count" to cat.count.toLong()
                    )
                    firestore.collection("categories").document(cat.id).set(map).await()
                }
                Result.success(defaultCategories)
            } else {
                val list = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("categoryID") ?: doc.id
                    val name = doc.getString("name") ?: ""
                    val emoji = doc.getString("emoji") ?: "📁"
                    val count = doc.getLong("count")?.toInt() ?: 0
                    
                    DocumentCategory(id, name, emoji, count)
                }
                Result.success(list)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể tải danh sách danh mục: ${e.localizedMessage}"))
        }
    }

    override suspend fun addCategory(name: String, emoji: String): Result<Unit> {
        return try {
            val id = firestore.collection("categories").document().id
            val map = hashMapOf(
                "categoryID" to id,
                "name" to name.trim(),
                "emoji" to emoji.trim(),
                "count" to 0L
            )
            firestore.collection("categories").document(id).set(map).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Thêm danh mục thất bại: ${e.localizedMessage}"))
        }
    }

    override suspend fun updateCategory(id: String, name: String, emoji: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "name" to name.trim(),
                "emoji" to emoji.trim()
            )
            firestore.collection("categories").document(id).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Cập nhật danh mục thất bại: ${e.localizedMessage}"))
        }
    }

    override suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            firestore.collection("categories").document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Xóa danh mục thất bại: ${e.localizedMessage}"))
        }
    }
}
