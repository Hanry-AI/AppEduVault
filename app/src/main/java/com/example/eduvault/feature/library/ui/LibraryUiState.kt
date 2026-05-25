package com.example.eduvault.feature.library.ui

/**
 * Trạng thái UI cho màn hình Thư viện tài liệu (Documents).
 */
data class LibraryUiState(
    val searchQuery: String = "",
    val selectedSubject: String = "Tất cả",
    val selectedDocType: DocTypeFilter = DocTypeFilter.ALL,
    val selectedSort: SortType = SortType.NEWEST,
    val selectedSchool: String = "Tất cả trường",
    val currentPage: Int = 1,
    val totalPages: Int = 12,
    val totalCount: Int = 156,
    val isGridView: Boolean = true,
    val isLoading: Boolean = false,
    val documentCredits: Int = 1,
    val uploadCount: Int = 0,
    val documents: List<LibraryDoc> = emptyList()
)

enum class SortType(val label: String) {
    NEWEST("Mới nhất"),
    POPULAR("Phổ biến"),
    RATING("Đánh giá")
}

enum class DocTypeFilter(val label: String) {
    ALL("Tất cả"),
    NOTE("Ghi chú"),
    QUIZ("Quiz"),
    SLIDE("Slide"),
    SUMMARY("Tóm tắt"),
    FLASHCARD("Flashcard")
}

enum class LibraryDocType(val label: String) {
    NOTE("Ghi chú"),
    QUIZ("Quiz"),
    SLIDE("Slide"),
    SUMMARY("Tóm tắt")
}

data class LibraryDoc(
    val id: String,
    val courseCode: String,
    val title: String,
    val type: LibraryDocType,
    val quantityLabel: String, // e.g. "42 tr", "30 câu", "18 slides"
    val rating: Float,
    val views: String, // e.g. "2.1k", "700"
    val isSaved: Boolean = false,
    val bgIndex: Int = 0
)
