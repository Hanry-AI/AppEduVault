package com.example.eduvault.feature.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.eduvault.domain.repository.AuthRepository

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private val allDocsList = mutableListOf<LibraryDoc>()

    // ─── High fidelity mock data matching user screenshots ────────────────────
    private val allMockDocs = listOf(
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

    init {
        allDocsList.addAll(allMockDocs)
        loadDocuments()
        loadUserCredits()
    }

    fun loadDocuments() {
        viewModelScope.launch {
            authRepository.getDocuments().onSuccess { firestoreDocs ->
                allDocsList.clear()
                allDocsList.addAll(allMockDocs)
                val existingIds = allMockDocs.map { it.id }.toSet()
                val uniqueFirestoreDocs = firestoreDocs.filter { it.id !in existingIds }
                allDocsList.addAll(uniqueFirestoreDocs)
                filterAndSortDocs()
            }.onFailure {
                filterAndSortDocs()
            }
        }
    }


    fun loadUserCredits() {
        viewModelScope.launch {
            authRepository.getCurrentUser().onSuccess { user ->
                _uiState.update { 
                    it.copy(
                        documentCredits = user?.documentCredits ?: 1,
                        uploadCount = user?.uploadCount ?: 0
                    )
                }
            }
        }
    }

    /**
     * Trừ credit khi mở tài liệu.
     */
    fun onOpenDocument(docId: String, onAllowed: () -> Unit, onBlocked: () -> Unit) {
        viewModelScope.launch {
            authRepository.consumeCredit().onSuccess { user ->
                _uiState.update {
                    it.copy(
                        documentCredits = user.documentCredits,
                        uploadCount = user.uploadCount
                    )
                }
                onAllowed()
            }.onFailure {
                onBlocked()
            }
        }
    }

    // ─── Event Handlers ───────────────────────────────────────────────────────

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 1) }
        filterAndSortDocs()
    }

    fun onSubjectSelected(subject: String) {
        _uiState.update { it.copy(selectedSubject = subject, currentPage = 1) }
        filterAndSortDocs()
    }

    fun onDocTypeSelected(filter: DocTypeFilter) {
        _uiState.update { it.copy(selectedDocType = filter, currentPage = 1) }
        filterAndSortDocs()
    }

    fun onSortSelected(sort: SortType) {
        _uiState.update { it.copy(selectedSort = sort) }
        filterAndSortDocs()
    }

    fun onSchoolSelected(school: String) {
        _uiState.update { it.copy(selectedSchool = school, currentPage = 1) }
        filterAndSortDocs()
    }

    fun onPageSelected(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }

    fun onToggleViewMode() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    fun onToggleSaved(docId: String) {
        _uiState.update { state ->
            val updatedDocs = state.documents.map { doc ->
                if (doc.id == docId) doc.copy(isSaved = !doc.isSaved) else doc
            }
            state.copy(documents = updatedDocs)
        }
    }

    // ─── Helper filter & sort logic ───────────────────────────────────────────

    private fun filterAndSortDocs() {
        viewModelScope.launch {
            val query = _uiState.value.searchQuery.trim().lowercase()
            val subject = _uiState.value.selectedSubject
            val typeFilter = _uiState.value.selectedDocType
            val sortType = _uiState.value.selectedSort

            // Filter
            var filtered = allDocsList.filter { doc ->
                val matchesSearch = query.isEmpty() ||
                        doc.title.lowercase().contains(query) ||
                        doc.courseCode.lowercase().contains(query)
                
                val matchesSubject = subject == "Tất cả" || when (subject) {
                    "Kinh tế vi mô" -> doc.courseCode.contains("EC")
                    "Marketing" -> doc.courseCode.contains("MKT")
                    "Kế toán" -> doc.courseCode.contains("ACC")
                    "Thống kê" -> doc.courseCode.contains("STAT")
                    "Quản trị" -> doc.courseCode.contains("MGT")
                    "Luật" -> doc.courseCode.contains("LAW")
                    else -> true
                }

                val matchesType = typeFilter == DocTypeFilter.ALL || when (typeFilter) {
                    DocTypeFilter.NOTE -> doc.type == LibraryDocType.NOTE
                    DocTypeFilter.QUIZ -> doc.type == LibraryDocType.QUIZ
                    DocTypeFilter.SLIDE -> doc.type == LibraryDocType.SLIDE
                    DocTypeFilter.SUMMARY -> doc.type == LibraryDocType.SUMMARY
                    else -> false
                }

                matchesSearch && matchesSubject && matchesType
            }

            // Sort
            filtered = when (sortType) {
                SortType.NEWEST -> filtered.sortedByDescending { it.id.toIntOrNull() ?: 0 }
                SortType.POPULAR -> filtered.sortedByDescending { it.views.replace("k", "000").replace(".", "").toIntOrNull() ?: 0 }
                SortType.RATING -> filtered.sortedByDescending { it.rating }
            }

            _uiState.update {
                it.copy(
                    documents = filtered,
                    totalCount = filtered.size,
                    totalPages = if (filtered.isEmpty()) 1 else (filtered.size + 5) / 6 // mock pagination size
                )
            }
        }
    }
}
