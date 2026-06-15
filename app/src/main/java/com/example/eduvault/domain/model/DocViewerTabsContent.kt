package com.example.eduvault.domain.model

/**
 * Model đại diện cho nội dung chi tiết của 3 tab động trong DocViewer.
 */
data class DocViewerTabsContent(
    val content: String,
    val keyPoints: String,
    val practice: String
)
