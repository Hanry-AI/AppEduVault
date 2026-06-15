package com.example.eduvault.data.repository

import com.example.eduvault.BuildConfig
import com.example.eduvault.domain.model.AiChatMessage
import com.example.eduvault.domain.repository.AiRepository
import com.example.eduvault.feature.quiz.ui.QuizAnswer
import com.example.eduvault.feature.quiz.ui.QuizQuestion
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Triển khai cụ thể của [AiRepository] gọi trực tiếp lên Google AI Gemini SDK.
 * - Sử dụng mẫu máy gemini-1.5-flash tối ưu tốc độ và dung lượng.
 * - Áp dụng Prompt Engineering nghiêm ngặt và cấu hình JSON Mode để giảm tối đa lỗi cú pháp.
 */
@Singleton
class AiRepositoryImpl @Inject constructor() : AiRepository {

    override suspend fun generateQuizFromDocument(
        title: String,
        type: String,
        content: String,
        count: Int,
        format: String
    ): Result<List<QuizQuestion>> = withContext(Dispatchers.IO) {
        try {
            // SECURITY BEST PRACTICE NOTE (Production vs Demo):
            // Trong đồ án học tập/bản demo này, Gemini API Key được lấy từ BuildConfig (lưu trong local.properties).
            // Đây là cách tiếp cận đơn giản và phù hợp để chạy thử cục bộ.
            // Tuy nhiên, đối với môi trường PRODUCTION thực tế:
            // 1. Tuyệt đối không được nhúng API Key trực tiếp vào Client App (dễ bị dịch ngược APK).
            // 2. Cần thiết lập một Backend Proxy Server đóng vai trò trung gian:
            //    App client -> Request (có HTTPS + Token) -> Backend Server (nắm giữ API Key an toàn) -> Google Gemini API.
            // 3. Backend Server sẽ thực hiện các biện pháp Rate Limiting và kiểm tra quyền trước khi ủy quyền gọi AI.
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "YOUR_API_KEY_HERE" || apiKey == "PLACEHOLDER_KEY") {
                return@withContext Result.failure(Exception("Cấu hình API Key trống hoặc không hợp lệ. Vui lòng thêm GEMINI_API_KEY vào local.properties!"))
            }

            // Ép Gemini trả về mảng JSON chuẩn mực bằng cấu hình responseMimeType
            val config = generationConfig {
                responseMimeType = "application/json"
            }
            val model = GenerativeModel(
                modelName = GEMINI_MODEL,
                apiKey = apiKey,
                generationConfig = config
            )

            val formatInstruction = when (format) {
                "Đúng / Sai" -> """
                    Định dạng câu hỏi: Đúng hoặc Sai (True/False).
                    Yêu cầu thiết kế riêng cho Đúng/Sai:
                    - Mỗi câu hỏi là một phát biểu/khẳng định học thuật đầy đủ từ tài liệu.
                    - Thuộc tính "options" CHỈ ĐƯỢC CHỨA ĐÚNG 2 đáp án bắt buộc có cấu trúc chính xác sau:
                      [
                        {"key": "A", "text": "Đúng"},
                        {"key": "B", "text": "Sai"}
                      ]
                    - Thuộc tính "correctOption" bắt buộc phải là "A" (nếu khẳng định đó là Đúng) hoặc "B" (nếu khẳng định đó là Sai).
                """.trimIndent()
                "Điền khuyết" -> """
                    Định dạng câu hỏi: Điền vào chỗ trống (Fill in the blank).
                    Yêu cầu thiết kế riêng cho Điền khuyết:
                    - Trong thuộc tính câu hỏi "text", hãy trình bày một câu lý thuyết hoặc công thức bị khuyết một từ/cụm từ quan trọng nhất, và ký hiệu chỗ khuyết đó bằng chuỗi "___" (ví dụ: "Mô hình 4P trong Marketing bao gồm Product, Price, Place và ___").
                    - Thuộc tính "options" phải chứa đúng 4 lựa chọn (A, B, C, D) tương ứng với các từ/cụm từ gợi ý dùng để điền vào vị trí "___".
                    - Thuộc tính "correctOption" là kí tự đáp án đúng (A hoặc B hoặc C hoặc D) chứa từ/cụm từ chính xác nhất để điền vào câu.
                """.trimIndent()
                else -> """
                    Định dạng câu hỏi: Trắc nghiệm khách quan truyền thống (Multiple Choice).
                    Yêu cầu thiết kế riêng cho Trắc nghiệm:
                    - Thuộc tính "options" phải chứa đúng 4 lựa chọn (A, B, C, D) tương ứng với 4 đáp án phân loại rõ ràng.
                    - Thuộc tính "correctOption" là kí tự viết hoa đáp án đúng (A hoặc B hoặc C hoặc D).
                """.trimIndent()
            }

            val prompt = """
                Bạn là một giảng viên đại học và trợ lý ôn tập học thuật chuyên nghiệp. 
                Nhiệm vụ của bạn là đọc và phân tích tài liệu sau đây để tạo một đề ôn tập gồm chính xác $count câu hỏi ôn luyện:
                
                Tiêu đề tài liệu: "$title"
                Loại tài liệu: "$type"
                Nội dung tài liệu: "$content"
                
                Yêu cầu thiết kế câu hỏi chung:
                1. Ngôn ngữ: Tiếng Việt chuẩn mực, rõ ràng, giàu tính sư phạm.
                2. Nội dung câu hỏi phải bám sát kiến thức cốt lõi, công thức hoặc các ý chính trong tài liệu, mang tính khách quan cao.
                3. Đề thi phải phân loại độ khó đa dạng.
                
                $formatInstruction
                
                Yêu cầu kỹ thuật định dạng đầu ra (RẤT QUAN TRỌNG):
                - Kết quả trả về PHẢI là một chuỗi JSON thuần chứa một mảng các đối tượng câu hỏi.
                - Tuyệt đối không bao bọc bởi mã định dạng Markdown như ```json hoặc văn bản chào hỏi, giải thích dư thừa nào khác ngoài chuỗi JSON.
                - Cấu trúc từng đối tượng câu hỏi trong mảng JSON phải đúng 100% theo schema sau:
                {
                  "id": "chuỗi ID tự tăng phân biệt, ví dụ: q1, q2, q3...",
                  "text": "Nội dung câu hỏi...",
                  "hint": "Gợi ý ngắn gọn hướng tư duy cho người học",
                  "options": [
                    {"key": "A", "text": "đáp án A"},
                    {"key": "B", "text": "đáp án B"}
                    ... (thêm C, D nếu là Trắc nghiệm hoặc Điền khuyết; dừng ở B nếu là Đúng/Sai)
                  ],
                  "correctOption": "kí tự viết hoa đáp án đúng (ví dụ: A hoặc B...)",
                  "explanation": "Giải thích chi tiết và học thuật vì sao đáp án đó là chính xác nhất dựa trên nội dung tài liệu"
                }
            """.trimIndent()

            val response = model.generateContent(prompt)
            val jsonText = response.text ?: return@withContext Result.failure(Exception("AI trả về phản hồi rỗng."))

            // Parse JSON an toàn kèm cơ chế Bắt lỗi định dạng (Fail-safe parsing)
            val questions = mutableListOf<QuizQuestion>()
            try {
                val cleanJson = jsonText.trim()
                val jsonArray = JSONArray(cleanJson)
                for (i in 0 until jsonArray.length()) {
                    val qObj = jsonArray.getJSONObject(i)
                    val id = qObj.optString("id", "q_${System.currentTimeMillis()}_$i")
                    val text = qObj.getString("text")
                    val hint = qObj.optString("hint", "Đọc kỹ đề bài để trả lời.")
                    val correctOption = qObj.getString("correctOption").trim().uppercase()
                    val explanation = qObj.getString("explanation")

                    val optArray = qObj.getJSONArray("options")
                    val parsedOptions = mutableListOf<QuizAnswer>()
                    for (j in 0 until optArray.length()) {
                        val oObj = optArray.getJSONObject(j)
                        parsedOptions.add(
                            QuizAnswer(
                                key = oObj.getString("key").trim().uppercase(),
                                text = oObj.getString("text")
                            )
                        )
                    }

                    // Validation: Đảm bảo số lượng options hợp lý theo format
                    val options = when {
                        format == "Đúng / Sai" && parsedOptions.size > 2 -> {
                            // Gemini hallucinate > 2 options cho Đúng/Sai → chỉ giữ 2 đầu
                            android.util.Log.w("AiRepository", "Đúng/Sai câu $i có ${parsedOptions.size} options, cắt về 2")
                            parsedOptions.take(2)
                        }
                        parsedOptions.isEmpty() -> {
                            // Không có options → bỏ qua câu này
                            android.util.Log.w("AiRepository", "Câu $i không có options, bỏ qua")
                            continue
                        }
                        else -> parsedOptions
                    }

                    questions.add(
                        QuizQuestion(
                            id = id,
                            text = text,
                            hint = hint,
                            options = options,
                            correctOption = correctOption,
                            explanation = explanation
                        )
                    )
                }
                Result.success(questions)
            } catch (jsonEx: Exception) {
                // Fail-safe: Ghi log lỗi và trả về một Result thất bại sạch thay vì làm crash app
                android.util.Log.e("AiRepository", "Mất cấu trúc JSON từ Gemini: ${jsonEx.localizedMessage}\nRaw text: $jsonText", jsonEx)
                Result.failure(Exception("Kết quả sinh từ AI bị sai định dạng cấu trúc đề thi. Vui lòng bấm thử lại."))
            }
        } catch (e: Exception) {
            android.util.Log.e("AiRepository", "Exception during Gemini quiz generation", e)
            Result.failure(Exception("Lỗi trong quá trình kết nối AI: ${e.localizedMessage}"))
        }
    }

    override suspend fun generateSummaryFromDocument(
        title: String,
        type: String,
        content: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "YOUR_API_KEY_HERE" || apiKey == "PLACEHOLDER_KEY") {
                return@withContext Result.failure(Exception("Cấu hình API Key trống hoặc không hợp lệ. Vui lòng cập nhật GEMINI_API_KEY trong local.properties!"))
            }

            val model = GenerativeModel(
                modelName = GEMINI_MODEL,
                apiKey = apiKey
            )

            val prompt = """
                Hãy viết một bản tóm tắt học thuật (Smart Summary) cực kỳ chuyên nghiệp, ngắn gọn và súc tích dựa trên tài liệu sau:
                Tiêu đề tài liệu: "$title"
                Loại tài liệu: "$type"
                Nội dung tài liệu: "$content"
                
                Yêu cầu cấu trúc bản tóm tắt:
                1. Mở đầu bằng 1 câu tóm tắt chủ đề tổng quan.
                2. Liệt kê đúng 3-4 ý gạch đầu dòng then chốt cực kỳ giá trị đại diện cho cốt lõi kiến thức của tài liệu.
                3. Đưa ra 1 lời khuyên học thuật (EduVault Study Tip) để ôn tập hiệu quả môn học này.
                4. Ngôn ngữ: Tiếng Việt, văn phong súc tích, tinh gọn.
                5. Định dạng: Trả về kết quả dạng Markdown sạch sẽ để hiển thị giao diện đẹp mắt (sử dụng in đậm, bullet points rõ ràng).
            """.trimIndent()

            val response = model.generateContent(prompt)
            val summaryText = response.text ?: return@withContext Result.failure(Exception("Gemini trả về phản hồi tóm tắt rỗng."))
            Result.success(summaryText.trim())
        } catch (e: Exception) {
            android.util.Log.e("AiRepository", "Exception during Gemini summary generation", e)
            Result.failure(Exception("Tạo tóm tắt thất bại: ${e.localizedMessage}"))
        }
    }

    override suspend fun askAiAboutDocument(
        title: String,
        type: String,
        content: String,
        question: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "YOUR_API_KEY_HERE" || apiKey == "PLACEHOLDER_KEY") {
                return@withContext Result.failure(Exception("Cấu hình API Key trống hoặc không hợp lệ. Vui lòng cập nhật GEMINI_API_KEY trong local.properties!"))
            }

            val model = GenerativeModel(
                modelName = GEMINI_MODEL,
                apiKey = apiKey
            )

            val prompt = """
                Bạn là một giảng viên đại học và Trợ lý Học tập Cá nhân (AI Tutor) vô cùng thông minh, tận tâm của hệ thống EduVault.
                Nhiệm vụ của bạn là giải đáp câu hỏi của học sinh dựa trên tài liệu học tập sau đây:
                
                Tiêu đề tài liệu: "$title"
                Loại tài liệu: "$type"
                Nội dung ngữ cảnh tài liệu: "$content"
                
                Câu hỏi của học sinh: "$question"
                
                Yêu cầu hướng dẫn học tập:
                1. Hãy trả lời trực tiếp vào trọng tâm câu hỏi một cách khoa học, sâu sắc và dễ hiểu bằng Tiếng Việt.
                2. Khuyến khích sử dụng ví dụ minh họa trực quan, định dạng Markdown sạch sẽ (gạch đầu dòng, từ khóa in đậm) để câu trả lời hiển thị đẹp mắt, trực quan và dễ tiếp thu cho học sinh.
                3. Văn phong: Giàu tính sư phạm, thân thiện, mang tính khích lệ tinh thần tự học.
            """.trimIndent()

            val response = model.generateContent(prompt)
            val responseText = response.text ?: return@withContext Result.failure(Exception("Gemini trả về phản hồi rỗng."))
            Result.success(responseText.trim())
        } catch (e: Exception) {
            android.util.Log.e("AiRepository", "Exception during Gemini Q&A tutoring", e)
            Result.failure(Exception("Không thể nhận câu trả lời từ AI: ${e.localizedMessage}"))
        }
    }

    override suspend fun askGeneralStudyAi(
        question: String,
        history: List<AiChatMessage>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "YOUR_API_KEY_HERE" || apiKey == "PLACEHOLDER_KEY") {
                return@withContext Result.failure(Exception("Cấu hình API Key trống hoặc không hợp lệ. Vui lòng cập nhật GEMINI_API_KEY trong local.properties!"))
            }

            val model = GenerativeModel(
                modelName = GEMINI_MODEL,
                apiKey = apiKey
            )

            // Chuẩn bị context lịch sử từ tối đa 6 tin nhắn gần nhất (tương đương 3 cặp hỏi-đáp)
            val recentHistory = history.takeLast(6)
            val historyContext = if (recentHistory.isNotEmpty()) {
                recentHistory.joinToString("\n") { msg ->
                    if (msg.isUser) "Học sinh: ${msg.text}" else "StudyAI: ${msg.text}"
                }
            } else {
                "Không có hội thoại trước đó."
            }

            val prompt = """
                Bạn là Trợ lý Học tập StudyAI của hệ thống EduVault. 
                Bạn là một cố vấn sư phạm vô cùng thông minh, tận tâm và giàu kinh nghiệm giảng dạy.
                
                Nhiệm vụ của bạn:
                - Giải đáp thắc mắc, câu hỏi của học sinh về bất kỳ môn học hay chủ đề học tập nào.
                - Trả lời trực tiếp vào trọng tâm câu hỏi một cách khoa học, sâu sắc nhưng cực kỳ dễ hiểu.
                - Ưu tiên sử dụng ví dụ minh họa thực tế trực quan để học sinh dễ tiếp thu.
                - Tuyệt đối trung thực, không bịa nguồn, không tự tạo thông tin sai lệch.
                - Giữ câu trả lời trong phạm vi giáo dục, học thuật. Nếu câu hỏi quá nhạy cảm hoặc không liên quan đến học tập, hãy khéo léo từ chối và hướng học sinh quay lại chủ đề ôn luyện.
                
                Yêu cầu định dạng phản hồi (RẤT QUAN TRỌNG):
                - Trả lời hoàn toàn bằng Tiếng Việt chuẩn mực.
                - Sử dụng định dạng Markdown sạch sẽ (gạch đầu dòng, từ khóa in đậm bằng dấu **, hoặc tiêu đề phụ ###) để câu trả lời hiển thị đẹp mắt và khoa học trên giao diện điện thoại.
                
                Ngữ cảnh lịch sử cuộc hội thoại trước đó (nếu có):
                $historyContext
                
                Câu hỏi mới của học sinh: "$question"
            """.trimIndent()

            val response = model.generateContent(prompt)
            val responseText = response.text ?: return@withContext Result.failure(Exception("Gemini trả về phản hồi rỗng."))
            Result.success(responseText.trim())
        } catch (e: Exception) {
            android.util.Log.e("AiRepository", "Exception during Gemini general Q&A", e)
            Result.failure(Exception("Không thể nhận phản hồi từ StudyAI: ${e.localizedMessage}"))
        }
    }

    override suspend fun generateDocViewerContent(
        title: String,
        courseCode: String,
        type: String
    ): Result<com.example.eduvault.domain.model.DocViewerTabsContent> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "YOUR_API_KEY_HERE" || apiKey == "PLACEHOLDER_KEY") {
                return@withContext Result.failure(Exception("Cấu hình API Key trống hoặc không hợp lệ. Vui lòng thêm GEMINI_API_KEY vào local.properties!"))
            }

            val config = generationConfig {
                responseMimeType = "application/json"
            }
            val model = GenerativeModel(
                modelName = GEMINI_MODEL,
                apiKey = apiKey,
                generationConfig = config
            )

            val prompt = """
                Bạn là một giảng viên đại học xuất sắc và nhà biên soạn giáo trình học thuật chuyên nghiệp.
                Nhiệm vụ của bạn là dựa vào tiêu đề, loại tài liệu và mã môn học dưới đây để soạn thảo ra nội dung học thuật chi tiết 100% thực tế cho người học:
                
                Tiêu đề tài liệu: "$title"
                Mã môn học: "$courseCode"
                Loại tài liệu: "$type"
                
                Hãy biên soạn 3 phần nội dung sau bằng Tiếng Việt học thuật chuẩn mực, súc tích:
                1. "content" (Nội dung chi tiết): Soạn thảo một bài giảng lý thuyết chuyên sâu, đầy đủ định nghĩa khoa học, công thức hoặc các mô hình phân tích liên quan bám sát tiêu đề. Định dạng Markdown đẹp mắt, chuyên nghiệp (Độ dài tối ưu: 200-300 từ).
                2. "keyPoints" (Trọng tâm kiến thức): Tóm gọn đúng 3-4 nguyên lý, quy tắc hoặc định lý then chốt nhất cần phải ghi nhớ dưới dạng gạch đầu dòng Markdown rõ ràng.
                3. "practice" (Luyện tập & Củng cố): Thiết kế 2-3 câu hỏi ôn tập tự luận ngắn hoặc bài tập tình huống thực tế kèm theo đáp án gợi ý giải thích chi tiết trực quan dạng Markdown.
                
                Yêu cầu kỹ thuật định dạng đầu ra (BẮT BUỘC):
                Kết quả trả về PHẢI là một chuỗi JSON thuần có cấu trúc chính xác sau:
                {
                  "content": "Nội dung bài giảng...",
                  "keyPoints": "Nội dung trọng tâm...",
                  "practice": "Nội dung bài tập luyện tập..."
                }
                Tuyệt đối không bao bọc bởi ```json hay bất kỳ văn bản chào hỏi nào khác ngoài chuỗi JSON.
            """.trimIndent()

            val response = model.generateContent(prompt)
            val rawText = response.text ?: return@withContext Result.failure(Exception("AI phản hồi rỗng."))
            
            // JSON Sanitizer an toàn
            val cleanJson = sanitizeJsonResponse(rawText)

            kotlin.runCatching {
                val jObj = org.json.JSONObject(cleanJson)
                com.example.eduvault.domain.model.DocViewerTabsContent(
                    content = jObj.getString("content").trim(),
                    keyPoints = jObj.getString("keyPoints").trim(),
                    practice = jObj.getString("practice").trim()
                )
            }.onSuccess { parsed ->
                return@withContext Result.success(parsed)
            }.onFailure { err ->
                android.util.Log.e("AiRepository", "Lỗi cấu trúc JSON từ Gemini: ${err.localizedMessage}\nRaw text: $rawText")
                return@withContext Result.failure(Exception("Định dạng dữ liệu học thuật bị sai cấu trúc. Vui lòng thử lại."))
            }
            
            Result.failure(Exception("Lỗi xử lý nội dung AI."))
        } catch (e: Exception) {
            android.util.Log.e("AiRepository", "Lỗi sinh nội dung DocViewer", e)
            Result.failure(Exception("Không thể sinh nội dung tài liệu bằng AI: ${e.localizedMessage}"))
        }
    }

    private fun sanitizeJsonResponse(rawText: String): String {
        var clean = rawText.trim()
        if (clean.startsWith("```")) {
            // Loại bỏ dòng ```json hoặc ``` ở đầu
            clean = clean.substringAfter("\n")
        }
        if (clean.endsWith("```")) {
            // Loại bỏ ``` ở cuối
            clean = clean.substringBeforeLast("```")
        }
        return clean.trim()
    }

    companion object {
        private const val GEMINI_MODEL = "gemini-2.5-flash"
    }
}
