package com.example.eduvault.feature.home.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel cho màn hình Home.
 *
 * Quy tắc tuân thủ:
 * - Không có Context, Activity, Fragment, hoặc View nào trong class này.
 * - Trạng thái UI được quản lý 100% qua StateFlow.
 * - TODO: Inject Repository khi hoàn thiện tầng data.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // ─── Sample data (sẽ thay bằng repository) ───────────────────────────────

    val recentDocs = listOf(
        RecentDoc("1", "Kinh tế vi mô — Chương 3: Cung & Cầu", "ECON101", DocType.NOTE, "📝", 1240, 87, 24, 0),
        RecentDoc("2", "Quiz Toán Kinh tế Cuối kỳ", "MATH201", DocType.QUIZ, "🧩", 543, 32, 20, 1),
        RecentDoc("3", "Marketing Mix — 4P Framework", "MKT301", DocType.SLIDE, "📊", 892, 64, 35, 2),
        RecentDoc("4", "Kế toán tài chính — Bảng cân đối", "ACC101", DocType.NOTE, "📋", 421, 29, 18, 3),
        RecentDoc("5", "Tóm tắt Pháp luật kinh doanh", "LAW201", DocType.SUMMARY, "⚖️", 678, 51, 12, 4),
        RecentDoc("6", "Thống kê — Kiểm định giả thuyết", "STAT102", DocType.NOTE, "📈", 334, 27, 28, 5),
    )

    val activityItems = listOf(
        ActivityItem("1", "Ghi chú Kinh tế vi mô Ch.4", "ECON101 · Đã chỉnh sửa", "2 phút trước", ActivityType.NOTE, 0.72f),
        ActivityItem("2", "Quiz Marketing — Chương 2", "MKT301 · 16/20 câu đúng", "1 giờ trước", ActivityType.QUIZ, 0.80f),
        ActivityItem("3", "Slide Kế toán tài chính", "ACC101 · Đã xem 18 trang", "3 giờ trước", ActivityType.SLIDE, 0.51f),
        ActivityItem("4", "Tóm tắt Pháp luật kinh doanh", "LAW201 · Đã đọc", "Hôm qua", ActivityType.NOTE, 1f),
        ActivityItem("5", "Quiz Toán Kinh tế — Thực hành", "MATH201 · 9/12 câu đúng", "Hôm qua", ActivityType.QUIZ, 0.75f),
    )

    val topicTags = listOf(
        TopicTag("Kinh tế học", 124),
        TopicTag("Marketing", 89),
        TopicTag("Kế toán", 67),
        TopicTag("Pháp luật", 45),
        TopicTag("Thống kê", 112),
        TopicTag("Tài chính", 78),
        TopicTag("Quản trị", 93),
        TopicTag("Ngoại ngữ", 56),
        TopicTag("Công nghệ", 34),
        TopicTag("Tâm lý học", 41),
    )

    // ─── Event handlers ───────────────────────────────────────────────────────

    fun onTabSelected(tab: HomeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}
