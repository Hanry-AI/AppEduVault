package com.example.eduvault.domain.model

/**
 * Model dữ liệu đại diện cho một tin nhắn trong cuộc hội thoại với Trợ lý AI.
 */
data class AiChatMessage(
    val isUser: Boolean,
    val text: String
)
