package com.example.eduvault

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class ExampleUnitTest {
    
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    /**
     * Kịch bản 1: Giả lập Race Condition khi 2 tài khoản cùng báo cáo (report) tài liệu
     * bằng phương pháp thủ công: Đọc giá trị cũ từ client -> Tăng 1 -> Ghi đè lên Firestore.
     * 
     * Kết quả mong muốn: Bị thất thoát lượt report do bất đồng bộ (Race Condition).
     */
    @Test
    fun testRaceCondition_NonAtomicUpdate() = runBlocking {
        var reportCount = 5L // Giá trị ban đầu trên database giả lập
        val lock = Any()

        // Mô phỏng 2 User (A và B) cùng gửi báo cáo đồng thời
        val job1 = launch(Dispatchers.Default) {
            // Bước 1: Đọc giá trị hiện tại
            val current = reportCount
            // Mô phỏng độ trễ mạng ngẫu nhiên trước khi cập nhật
            delay(50)
            // Bước 2: Tăng 1 và ghi ngược lại
            synchronized(lock) {
                reportCount = current + 1L
            }
        }

        val job2 = launch(Dispatchers.Default) {
            // Bước 1: Đọc giá trị hiện tại (cả User B cũng đọc được 5L vì User A chưa kịp ghi đè)
            val current = reportCount
            delay(50)
            // Bước 2: Tăng 1 và ghi ngược lại
            synchronized(lock) {
                reportCount = current + 1L
            }
        }

        job1.join()
        job2.join()

        // Vì xảy ra race condition, reportCount sẽ chỉ tăng lên 6 thay vì 7!
        println("Non-Atomic reportCount: $reportCount (Ban đầu: 5, Đáng lẽ phải là 7)")
        assertEquals(6L, reportCount)
    }

    /**
     * Kịch bản 2: Giả lập cơ chế Atomic Increment tương đương với FieldValue.increment(1) của Firestore.
     * Cả 2 luồng cùng gửi yêu cầu tăng giá trị một cách nguyên tử trực tiếp tại server,
     * không phụ thuộc vào giá trị đọc được ở client.
     * 
     * Kết quả mong muốn: Giá trị tăng chính xác lên 7, không bị Race Condition.
     */
    @Test
    fun testConcurrentAtomicIncrement() = runBlocking {
        val reportCount = AtomicInteger(5) // Đại diện cho Firestore Atomic Increment

        // Mô phỏng 2 User (A và B) gửi báo cáo đồng thời
        val job1 = launch(Dispatchers.Default) {
            delay(50)
            reportCount.incrementAndGet() // Tương đương docRef.update("reportCount", FieldValue.increment(1))
        }

        val job2 = launch(Dispatchers.Default) {
            delay(50)
            reportCount.incrementAndGet() // Tương đương docRef.update("reportCount", FieldValue.increment(1))
        }

        job1.join()
        job2.join()

        // Nhờ tính nguyên tử (Atomic), kết quả luôn luôn chính xác là 7!
        println("Atomic reportCount: ${reportCount.get()} (Ban đầu: 5, Kết quả chính xác: 7)")
        assertEquals(7, reportCount.get())
    }
}

