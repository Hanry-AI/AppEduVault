package com.example.eduvault.feature.quiz.ui

/**
 * Độ khó của bộ đề trắc nghiệm
 */
enum class Difficulty(val label: String) {
    EASY("Dễ"),
    MEDIUM("T.Bình"),
    HARD("Khó")
}

/**
 * Bộ đề trắc nghiệm (Quiz Set)
 */
data class QuizSet(
    val id: String,
    val subject: String,
    val title: String,
    val difficulty: Difficulty,
    val questionCount: Int,
    val playCount: String,
    val isNew: Boolean = false,
    val bgIndex: Int = 0
)

/**
 * Người dùng trong bảng xếp hạng (Leaderboard)
 */
data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val university: String,
    val score: Int,
    val isMe: Boolean = false
)

/**
 * Thống kê tiến độ luyện tập
 */
data class QuizProgress(
    val avgScore: Float, // e.g. 8.5
    val completedCount: Int, // e.g. 18
    val completionRate: Int // e.g. 92%
)

/**
 * Lựa chọn đáp án trắc nghiệm
 */
data class QuizAnswer(
    val key: String, // "A", "B", "C", "D"
    val text: String
)

/**
 * Câu hỏi trắc nghiệm
 */
data class QuizQuestion(
    val id: String,
    val text: String,
    val hint: String = "",
    val options: List<QuizAnswer>,
    val correctOption: String, // "A", "B", "C", "D"
    val explanation: String
)

/**
 * Các chế độ màn hình trắc nghiệm
 */
enum class QuizScreenMode {
    DASHBOARD,
    PLAYER,
    RESULTS
}

/**
 * Trạng thái UI đầy đủ cho màn hình Quiz AI
 */
data class QuizUiState(
    val screenMode: QuizScreenMode = QuizScreenMode.DASHBOARD,
    val selectedSubject: String = "Tất cả",
    val isLoading: Boolean = false,

    // Dashboard state
    val quizSets: List<QuizSet> = emptyList(),
    val progress: QuizProgress = QuizProgress(8.5f, 18, 92),
    val leaderboard: List<LeaderboardUser> = emptyList(),

    // Player state
    val activeQuizSet: QuizSet? = null,
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOption: String? = null, // Đáp án đã chọn hiện tại ("A", "B", "C", "D")
    val isAnswerLocked: Boolean = false, // Khóa đáp án sau khi người dùng đã chọn
    val remainingSeconds: Int = 60, // Đếm ngược thời gian cho câu hỏi hiện tại
    val answersMap: Map<Int, String?> = emptyMap(), // Ánh xạ từ chỉ mục câu hỏi -> đáp án đã chọn
    val correctAnswersCount: Int = 0,
    val timeSpentSeconds: Int = 0, // Tổng thời gian đã làm bài
    val xpEarned: Int = 0
)
