package com.example.eduvault.domain.model

/**
 * Domain model cho danh mục tài liệu.
 */
data class DocumentCategory(
    val id: String,
    val name: String,
    val emoji: String = "📁",
    val count: Int = 0
)
