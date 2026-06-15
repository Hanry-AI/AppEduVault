package com.example.eduvault.core.mock

import com.example.eduvault.feature.library.ui.LibraryDoc
import com.example.eduvault.feature.library.ui.LibraryDocType

/**
 * Lớp cung cấp dữ liệu mock dùng chung cho toàn dự án.
 * Giúp tránh trùng lặp mã nguồn và lệch dữ liệu giữa các thành phần khác nhau.
 */
object MockDataProvider {
    val allMockDocs = listOf(
        LibraryDoc("1", "EC0201", "Kinh tế vi mô — Tổng hợp lý thuyết cầu, cung & thị trường", LibraryDocType.NOTE, "42 tr", 4.8f, "2.1k", bgIndex = 0),
        LibraryDoc("2", "MKT301", "Nguyên lý Marketing — Bộ câu hỏi luyện thi cuối kỳ", LibraryDocType.QUIZ, "30 câu", 4.9f, "3.4k", isSaved = true, bgIndex = 1),
        LibraryDoc("3", "ACC101", "Kế toán tài chính — Slide bài giảng chương 5, 6, 7", LibraryDocType.SLIDE, "18 slides", 4.7f, "1.8k", bgIndex = 2),
        LibraryDoc("4", "STAT202", "Thống kê ứng dụng — Công thức & bài tập mẫu chương 1-4", LibraryDocType.SUMMARY, "15 tr", 5.0f, "5.2k", isSaved = true, bgIndex = 3),
        LibraryDoc("5", "MGT401", "Quản trị chiến lược — Phân tích SWOT, Porter & Case study", LibraryDocType.NOTE, "28 tr", 4.6f, "1.2k", bgIndex = 4),
        LibraryDoc("6", "LAW201", "Luật kinh doanh — Ôn thi cuối kỳ dạng trắc nghiệm", LibraryDocType.QUIZ, "25 câu", 4.8f, "700", bgIndex = 5),
        LibraryDoc("7", "FIN301", "Tài chính doanh nghiệp — Tổng hợp lý thuyết & bài tập có lời giải", LibraryDocType.NOTE, "36 tr", 4.9f, "4.1k", bgIndex = 0),
        LibraryDoc("8", "EC0101", "Kinh tế học đại cương — Slide tổng hợp toàn bộ học kỳ", LibraryDocType.SLIDE, "24 slides", 4.5f, "950", bgIndex = 1),
        LibraryDoc("9", "MKT201", "Hành vi người dùng — Tóm tắt lý thuyết chính yếu", LibraryDocType.SUMMARY, "10 tr", 4.7f, "2.3k", isSaved = true, bgIndex = 2)
    )
}
