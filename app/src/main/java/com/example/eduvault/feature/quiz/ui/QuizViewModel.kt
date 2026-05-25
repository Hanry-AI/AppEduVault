package com.example.eduvault.feature.quiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.eduvault.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _availableDocs = MutableStateFlow<List<com.example.eduvault.feature.library.ui.LibraryDoc>>(emptyList())
    val availableDocs: StateFlow<List<com.example.eduvault.feature.library.ui.LibraryDoc>> = _availableDocs.asStateFlow()


    private var timerJob: Job? = null

    // ─── High Fidelity Vietnamese Mock Data matching user screenshots ──────────
    private val allMockQuizSets = listOf(
        QuizSet("1", "EC0201 - Kinh tế vi mô", "Kinh tế vi mô — Trắc nghiệm chương Cung Cầu & Thị trường", Difficulty.EASY, 5, "1.2k", isNew = true, bgIndex = 0),
        QuizSet("2", "MKT301 - Marketing", "Nguyên lý Marketing — Bộ câu hỏi ôn luyện lý thuyết cốt lõi", Difficulty.MEDIUM, 5, "800", bgIndex = 1),
        QuizSet("3", "STAT202 - Thống kê", "Thống kê xã hội học — Ôn tập phân phối xác suất & mẫu số", Difficulty.HARD, 5, "450", bgIndex = 2),
        QuizSet("4", "LAW101 - Luật đại cương", "Pháp luật đại cương — Hệ thống văn bản & bộ máy nhà nước", Difficulty.EASY, 5, "1.5k", bgIndex = 3),
        QuizSet("5", "FIN301 - Tài chính", "Tài chính doanh nghiệp — Bài tập giá trị thời gian của tiền", Difficulty.MEDIUM, 5, "620", isNew = true, bgIndex = 4),
        QuizSet("6", "BIO102 - Vi sinh học", "Vi sinh học đại cương — Cấu trúc tế bào & trao đổi chất", Difficulty.MEDIUM, 5, "280", bgIndex = 5)
    )

    private val mockLeaderboard = listOf(
        LeaderboardUser(1, "Nguyễn Văn Anh", "Đại học Bách Khoa", 12450),
        LeaderboardUser(2, "Trần Thị Bình", "Đại học Kinh tế Quốc dân", 11200),
        LeaderboardUser(3, "Lê Hoàng Cường", "Đại học Ngoại thương", 9850),
        LeaderboardUser(4, "Phạm Minh Đức", "Đại học FPT", 9100),
        LeaderboardUser(5, "Đỗ Thanh Hải", "Đại học Quốc gia", 8600),
        LeaderboardUser(12, "Gamer (Tôi)", "Đại học Công nghệ thông tin", 7200, isMe = true)
    )

    // Bộ câu hỏi cho môn Kinh tế vi mô (id = "1")
    private val microQuestions = listOf(
        QuizQuestion(
            id = "q1_1",
            text = "Đường cầu của một hàng hóa thường dốc xuống dưới từ trái sang phải là do:",
            hint = "Gợi ý: Hãy nghĩ tới mức độ thỏa mãn tăng thêm khi mua thêm một sản phẩm.",
            options = listOf(
                QuizAnswer("A", "Quy luật lợi ích cận biên giảm dần"),
                QuizAnswer("B", "Thu nhập của người tiêu dùng ngày càng tăng"),
                QuizAnswer("C", "Giá cả hàng hóa thay thế tăng lên"),
                QuizAnswer("D", "Chi phí sản xuất biên ngày càng tăng")
            ),
            correctOption = "A",
            explanation = "Theo quy luật lợi ích cận biên giảm dần, khi tiêu dùng thêm một đơn vị sản phẩm, mức độ thỏa dụng tăng thêm (lợi ích cận biên) sẽ giảm đi. Do đó, người tiêu dùng chỉ sẵn sàng mua thêm sản phẩm tiếp theo ở mức giá thấp hơn."
        ),
        QuizQuestion(
            id = "q1_2",
            text = "Khi giá của hàng hóa X tăng lên làm giảm lượng cầu đối với hàng hóa Y thì X và Y là:",
            hint = "Gợi ý: Cầu giảm khi giá của sản phẩm đi kèm tăng.",
            options = listOf(
                QuizAnswer("A", "Hai hàng hóa thay thế nhau"),
                QuizAnswer("B", "Hai hàng hóa bổ sung cho nhau"),
                QuizAnswer("C", "Hai hàng hóa độc lập hoàn toàn"),
                QuizAnswer("D", "Hai hàng hóa thứ cấp")
            ),
            correctOption = "B",
            explanation = "Hai hàng hóa là bổ sung nếu việc tăng giá của một hàng hóa kéo theo việc giảm lượng cầu của hàng hóa kia (do chúng thường được tiêu dùng cùng nhau, ví dụ: xăng và xe máy)."
        ),
        QuizQuestion(
            id = "q1_3",
            text = "Trạng thái cân bằng của thị trường đạt được khi nào?",
            hint = "Gợi ý: Tại mức giá cân bằng, cung gặp cầu.",
            options = listOf(
                QuizAnswer("A", "Lượng cung đúng bằng lượng cầu tại một mức giá xác định"),
                QuizAnswer("B", "Mọi người bán đều bán hết sạch hàng hóa mang đi"),
                QuizAnswer("C", "Mức giá bán ra đạt trạng thái thấp nhất lịch sử"),
                QuizAnswer("D", "Chính phủ áp đặt mức giá trần bắt buộc")
            ),
            correctOption = "A",
            explanation = "Trạng thái cân bằng thị trường xảy ra tại điểm giao nhau giữa đường cung và đường cầu, nghĩa là lượng người bán muốn bán đúng bằng lượng người mua muốn mua tại mức giá cân bằng thị trường."
        ),
        QuizQuestion(
            id = "q1_4",
            text = "Hệ số co giãn của cầu theo giá đối với một hàng hóa là -2 có nghĩa là gì?",
            hint = "Gợi ý: Xem xét tỷ lệ phần trăm thay đổi của lượng cầu so với giá.",
            options = listOf(
                QuizAnswer("A", "Giá hàng hóa tăng 10% làm cho lượng cầu giảm 20%"),
                QuizAnswer("B", "Lượng cầu hàng hóa tăng 10% làm cho giá giảm 20%"),
                QuizAnswer("C", "Giá hàng hóa giảm 10% làm cho lượng cầu giảm 20%"),
                QuizAnswer("D", "Giá hàng hóa tăng 10% làm cho lượng cầu tăng 20%")
            ),
            correctOption = "A",
            explanation = "Hệ số co giãn cầu theo giá phản ánh tốc độ thay đổi lượng cầu khi giá biến động. Với hệ số bằng -2 (trị tuyệt đối bằng 2), giá tăng 10% sẽ làm cầu giảm đi 10% * 2 = 20%."
        ),
        QuizQuestion(
            id = "q1_5",
            text = "Một doanh nghiệp tối đa hóa lợi nhuận của mình khi sản xuất ở mức sản lượng nào?",
            hint = "Gợi ý: So sánh doanh thu thu thêm và chi phí bỏ thêm cho đơn vị sản phẩm cuối cùng.",
            options = listOf(
                QuizAnswer("A", "Doanh thu biên bằng chi phí biên (MR = MC)"),
                QuizAnswer("B", "Doanh thu tổng thể đạt trị số lớn nhất"),
                QuizAnswer("C", "Tổng chi phí sản xuất được cắt giảm tối đa"),
                QuizAnswer("D", "Giá bán hàng hóa được đẩy lên cao nhất")
            ),
            correctOption = "A",
            explanation = "Nguyên lý tối đa hóa lợi nhuận của doanh nghiệp (cho cả cạnh tranh hoàn hảo lẫn độc quyền) yêu cầu sản xuất tại điểm mà Doanh thu cận biên (MR) bằng Chi phí cận biên (MC). Nếu sản xuất nhiều hơn hoặc ít hơn điểm này, lợi nhuận đều sẽ bị giảm sút."
        )
    )

    // Bộ câu hỏi cho môn Marketing (id = "2")
    private val marketingQuestions = listOf(
        QuizQuestion(
            id = "q2_1",
            text = "Bước đầu tiên và quan trọng nhất trong quá trình nghiên cứu Marketing là gì?",
            hint = "Gợi ý: Phải biết rõ mình cần tìm câu trả lời cho vấn đề gì trước đã.",
            options = listOf(
                QuizAnswer("A", "Xác định rõ ràng vấn đề và mục tiêu nghiên cứu cụ thể"),
                QuizAnswer("B", "Xây dựng kế hoạch thu thập thông tin"),
                QuizAnswer("C", "Triển khai thu thập dữ liệu trên diện rộng"),
                QuizAnswer("D", "Phân tích số liệu và viết báo cáo kết quả")
            ),
            correctOption = "A",
            explanation = "Xác định chính xác vấn đề và mục tiêu giúp tiết kiệm ngân sách, định hướng đúng đắn tất cả các khâu sau này của dự án nghiên cứu nghiên cứu marketing."
        ),
        QuizQuestion(
            id = "q2_2",
            text = "Hệ thống Marketing hỗn hợp truyền thống (4P Mix) bao gồm các yếu tố nào?",
            hint = "Gợi ý: Bốn trụ cột bán hàng cốt lõi.",
            options = listOf(
                QuizAnswer("A", "Product, Price, Place, Promotion"),
                QuizAnswer("B", "People, Price, Place, Promotion"),
                QuizAnswer("C", "Product, Price, Position, Promotion"),
                QuizAnswer("D", "Product, Packaging, Place, Promotion")
            ),
            correctOption = "A",
            explanation = "Mô hình 4P cổ điển đề xuất bởi McCarthy bao gồm: Product (Sản phẩm), Price (Giá cả), Place (Phân phối), và Promotion (Chiêu thị / Xúc tiến bán hàng)."
        ),
        QuizQuestion(
            id = "q2_3",
            text = "Thuật ngữ 'Phân khúc thị trường' (Market Segmentation) được hiểu là:",
            hint = "Gợi ý: Chia nhỏ thị trường lớn thành các nhóm nhỏ tương đồng.",
            options = listOf(
                QuizAnswer("A", "Chia thị trường lớn không đồng nhất thành các nhóm nhỏ đồng nhất hơn về nhu cầu"),
                QuizAnswer("B", "Tập trung truyền thông sản phẩm đến tất cả mọi tầng lớp nhân dân"),
                QuizAnswer("C", "Lựa chọn một tệp khách hàng mục tiêu duy nhất để kinh doanh"),
                QuizAnswer("D", "Định vị hình ảnh thương hiệu trong tâm trí người dùng")
            ),
            correctOption = "A",
            explanation = "Phân khúc thị trường là bước phân chia một thị trường không đồng nhất thành các phần thị trường (phân khúc) đồng nhất hơn về mặt nhân khẩu, địa lý hoặc hành vi để có thể thiết kế các chiến dịch phù hợp."
        ),
        QuizQuestion(
            id = "q2_4",
            text = "Một chu kỳ sống điển hình của sản phẩm (Product Life Cycle) diễn ra theo trật tự nào?",
            hint = "Gợi ý: Từ khi thai nghén ra mắt đến lúc kết thúc.",
            options = listOf(
                QuizAnswer("A", "Triển khai ra mắt, Tăng trưởng nhanh, Bão hòa doanh số, Suy thoái"),
                QuizAnswer("B", "Hình thành ý tưởng, Nghiên cứu thử nghiệm, Sản xuất đại trà, Bán hàng"),
                QuizAnswer("C", "Tăng trưởng nóng, Triển khai thử nghiệm, Bão hòa, Băng hoại"),
                QuizAnswer("D", "Giới thiệu sản phẩm, Bão hòa thị trường, Phát triển, Suy thoái")
            ),
            correctOption = "A",
            explanation = "Đại đa số sản phẩm thương mại đều đi qua 4 giai đoạn tiêu chuẩn: Giới thiệu ra mắt (Introduction), Phát triển gia tốc (Growth), Đạt đỉnh bão hòa (Maturity), và kết thúc suy thoái thoái lui (Decline)."
        ),
        QuizQuestion(
            id = "q2_5",
            text = "Thương hiệu (Brand) mang lại giá trị cốt lõi nào cho khách hàng tiêu dùng?",
            hint = "Gợi ý: Tại sao người mua chuộng sản phẩm có tên tuổi hơn hàng vô danh?",
            options = listOf(
                QuizAnswer("A", "Giúp nhận diện nguồn gốc, tạo niềm tin và giảm thiểu rủi ro mua sắm"),
                QuizAnswer("B", "Làm cho sản phẩm trở nên thời thượng và có giá bán cao gấp đôi"),
                QuizAnswer("C", "Ràng buộc khách hàng bắt buộc phải trung thành lâu dài"),
                QuizAnswer("D", "Cung cấp chi tiết tất cả các thông số kỹ thuật bên trong sản phẩm")
            ),
            correctOption = "A",
            explanation = "Giá trị cốt lõi nhất của thương hiệu là nhận diện nguồn gốc xuất xứ, thiết lập chất lượng bảo đảm giúp người mua an tâm, giảm bớt chi phí tìm kiếm thông tin và giảm thiểu rủi ro mua nhầm hàng kém chất lượng."
        )
    )

    init {
        // Tải danh sách bộ đề ban đầu
        _uiState.update {
            it.copy(
                quizSets = allMockQuizSets,
                leaderboard = mockLeaderboard
            )
        }
        loadAvailableDocuments()
    }

    fun loadAvailableDocuments() {
        viewModelScope.launch {
            val baselineMockDocs = listOf(
                com.example.eduvault.feature.library.ui.LibraryDoc("m1", "EC0201", "Kinh tế vi mô — Chương 3: Cung & Cầu", com.example.eduvault.feature.library.ui.LibraryDocType.NOTE, "42 tr", 4.8f, "2.1k", bgIndex = 0),
                com.example.eduvault.feature.library.ui.LibraryDoc("m2", "MKT301", "Nguyên lý Marketing — 4P Framework", com.example.eduvault.feature.library.ui.LibraryDocType.QUIZ, "30 câu", 4.9f, "3.4k", bgIndex = 1),
                com.example.eduvault.feature.library.ui.LibraryDoc("m3", "ACC101", "Toán Kinh tế — Giải tích & Mẫu số", com.example.eduvault.feature.library.ui.LibraryDocType.SLIDE, "18 slides", 4.7f, "1.8k", bgIndex = 2),
                com.example.eduvault.feature.library.ui.LibraryDoc("m4", "LAW101", "Pháp luật đại cương — Hệ thống văn bản pháp luật", com.example.eduvault.feature.library.ui.LibraryDocType.SUMMARY, "15 tr", 5.0f, "5.2k", bgIndex = 3),
                com.example.eduvault.feature.library.ui.LibraryDoc("m5", "FIN301", "Tài chính doanh nghiệp — Bài tập giá trị thời gian", com.example.eduvault.feature.library.ui.LibraryDocType.NOTE, "28 tr", 4.6f, "1.2k", bgIndex = 4)
            )
            _availableDocs.value = baselineMockDocs
            
            authRepository.getDocuments().onSuccess { firestoreDocs ->
                val combined = baselineMockDocs.toMutableList()
                val existingIds = baselineMockDocs.map { it.id }.toSet()
                val uniqueFirestoreDocs = firestoreDocs.filter { it.id !in existingIds }
                combined.addAll(uniqueFirestoreDocs)
                _availableDocs.value = combined
            }
        }
    }


    // ─── Event Handlers ───────────────────────────────────────────────────────

    /**
     * Lọc danh sách bộ đề theo chủ đề môn học
     */
    fun onSubjectSelected(subject: String) {
        _uiState.update { state ->
            val filtered = if (subject == "Tất cả") {
                allMockQuizSets
            } else {
                allMockQuizSets.filter { it.subject.contains(subject, ignoreCase = true) }
            }
            state.copy(
                selectedSubject = subject,
                quizSets = filtered
            )
        }
    }

    /**
     * Bắt đầu chơi bộ đề trắc nghiệm
     */
    fun startQuiz(quizSet: QuizSet) {
        // Tải câu hỏi tùy thuộc vào bộ đề đã chọn
        val rawQuestions = when {
            quizSet.subject.contains("Marketing", ignoreCase = true) || quizSet.id == "2" -> marketingQuestions
            else -> microQuestions // mặc định dùng bộ câu hỏi Kinh tế vi mô chất lượng cao
        }

        // Tạo danh sách câu hỏi có số lượng chính xác như yêu cầu bằng cách lặp lại/cắt lát câu hỏi gốc
        val finalQuestions = mutableListOf<QuizQuestion>()
        val count = quizSet.questionCount
        while (finalQuestions.size < count && rawQuestions.isNotEmpty()) {
            val remaining = count - finalQuestions.size
            finalQuestions.addAll(rawQuestions.take(remaining).mapIndexed { idx, q ->
                q.copy(id = "${q.id}_custom_${finalQuestions.size + idx}")
            })
        }

        _uiState.update {
            it.copy(
                screenMode = QuizScreenMode.PLAYER,
                activeQuizSet = quizSet,
                questions = finalQuestions,
                currentQuestionIndex = 0,
                selectedOption = null,
                isAnswerLocked = false,
                remainingSeconds = 60,
                answersMap = emptyMap(),
                correctAnswersCount = 0,
                timeSpentSeconds = 0,
                xpEarned = 0
            )
        }

        startTimer()
    }

    /**
     * Chọn phương án trả lời
     */
    fun selectOption(optionKey: String) {
        val state = _uiState.value
        if (state.isAnswerLocked) return // Không cho chọn lại sau khi đã khóa

        val currentQuestion = state.questions.getOrNull(state.currentQuestionIndex) ?: return
        val isCorrect = currentQuestion.correctOption == optionKey

        val updatedAnswersMap = state.answersMap.toMutableMap()
        updatedAnswersMap[state.currentQuestionIndex] = optionKey

        val updatedCorrectCount = if (isCorrect) state.correctAnswersCount + 1 else state.correctAnswersCount
        val xpReward = if (isCorrect) state.xpEarned + 20 else state.xpEarned // 20 XP cho mỗi câu đúng

        stopTimer() // Dừng bộ đếm ngược khi đã chọn đáp án

        _uiState.update {
            it.copy(
                selectedOption = optionKey,
                isAnswerLocked = true,
                answersMap = updatedAnswersMap,
                correctAnswersCount = updatedCorrectCount,
                xpEarned = xpReward
            )
        }
    }

    /**
     * Chuyển sang câu hỏi tiếp theo hoặc hiển thị màn hình kết quả
     */
    fun nextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex < state.questions.size) {
            // Chuyển sang câu hỏi kế tiếp
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedOption = null,
                    isAnswerLocked = false,
                    remainingSeconds = 60
                )
            }
            startTimer() // Khởi động lại đếm ngược cho câu hỏi mới
        } else {
            // Hoàn thành bộ đề -> Chuyển sang màn hình Kết Quả
            stopTimer()
            _uiState.update {
                it.copy(
                    screenMode = QuizScreenMode.RESULTS,
                    // Cập nhật điểm thưởng thêm 50 XP nếu đạt điểm tối đa
                    xpEarned = if (it.correctAnswersCount == it.questions.size) it.xpEarned + 50 else it.xpEarned
                )
            }
        }
    }

    /**
     * Bỏ qua câu hỏi hiện tại (Đánh dấu sai)
     */
    fun skipQuestion() {
        val state = _uiState.value
        if (state.isAnswerLocked) {
            // Nếu đã trả lời xong và bấm nút, hoạt động giống như phím Tiếp theo
            nextQuestion()
            return
        }

        // Đánh dấu bỏ qua là trả lời sai
        val updatedAnswersMap = state.answersMap.toMutableMap()
        updatedAnswersMap[state.currentQuestionIndex] = "" // Rỗng nghĩa là bỏ qua/không trả lời

        stopTimer()

        _uiState.update {
            it.copy(
                selectedOption = "",
                isAnswerLocked = true,
                answersMap = updatedAnswersMap
            )
        }
    }

    /**
     * Chơi lại bài trắc nghiệm hiện tại
     */
    fun retryQuiz() {
        val activeSet = _uiState.value.activeQuizSet
        if (activeSet != null) {
            startQuiz(activeSet)
        }
    }

    /**
     * Thoát bài trắc nghiệm, quay lại Dashboard chính
     */
    fun exitQuiz() {
        stopTimer()
        _uiState.update {
            it.copy(
                screenMode = QuizScreenMode.DASHBOARD,
                activeQuizSet = null,
                questions = emptyList(),
                currentQuestionIndex = 0,
                selectedOption = null,
                isAnswerLocked = false,
                answersMap = emptyMap(),
                correctAnswersCount = 0,
                timeSpentSeconds = 0,
                xpEarned = 0
            )
        }
    }

    // ─── Timer Logic ──────────────────────────────────────────────────────────

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    val nextRemaining = state.remainingSeconds - 1
                    val nextTimeSpent = state.timeSpentSeconds + 1

                    if (nextRemaining <= 0) {
                        // Hết thời gian -> Tự động khóa đáp án là sai để hiển thị giải thích
                        stopTimer()
                        val updatedAnswersMap = state.answersMap.toMutableMap()
                        updatedAnswersMap[state.currentQuestionIndex] = "" // Đáp án rỗng

                        state.copy(
                            remainingSeconds = 0,
                            timeSpentSeconds = nextTimeSpent,
                            selectedOption = "",
                            isAnswerLocked = true,
                            answersMap = updatedAnswersMap
                        )
                    } else {
                        state.copy(
                            remainingSeconds = nextRemaining,
                            timeSpentSeconds = nextTimeSpent
                        )
                    }
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
