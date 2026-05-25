package com.example.eduvault.data.repository

import com.example.eduvault.domain.model.User
import com.example.eduvault.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Triển khai thực của [AuthRepository] sử dụng Firebase Auth + Firestore.
 *
 * Quy tắc tuân thủ:
 * - Class này chứa toàn bộ logic Firebase, giữ cho ViewModel sạch.
 * - Mọi lỗi từ Firebase đều được bắt và chuyển thành [Result.failure] với message tiếng Việt.
 * - Thông tin user được lưu trong Firestore collection "users" với document ID = UID.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Đăng nhập thất bại. Vui lòng thử lại."))

            // Lấy thêm thông tin đầy đủ từ Firestore
            val user = fetchUserFromFirestore(firebaseUser.uid)
                ?: User(
                    uid = firebaseUser.uid,
                    fullName = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: email,
                )

            Result.success(user)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Email hoặc mật khẩu không đúng. Vui lòng kiểm tra lại."))
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Tài khoản không tồn tại hoặc đã bị vô hiệu hóa."))
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    // ─── Register ─────────────────────────────────────────────────────────────

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
    ): Result<User> {
        return try {
            // Bước 1: Tạo tài khoản Firebase Auth
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Đăng ký thất bại. Vui lòng thử lại."))

            // Bước 2: Cập nhật displayName trong Firebase Auth profile
            val profileUpdates = userProfileChangeRequest {
                displayName = fullName
            }
            firebaseUser.updateProfile(profileUpdates).await()

            // Bước 3: Lưu thông tin đầy đủ vào Firestore
            val user = User(
                uid = firebaseUser.uid,
                fullName = fullName,
                email = email,
                createdAt = System.currentTimeMillis(),
            )
            saveUserToFirestore(user)

            Result.success(user)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.failure(Exception("Mật khẩu quá yếu. Vui lòng chọn mật khẩu mạnh hơn."))
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("Email này đã được đăng ký. Vui lòng dùng email khác hoặc đăng nhập."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Định dạng email không hợp lệ."))
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    // ─── Forgot Password ──────────────────────────────────────────────────────

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Email này chưa được đăng ký trong hệ thống."))
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    // ─── Get Current User ─────────────────────────────────────────────────────

    override suspend fun getCurrentUser(): Result<User?> {
        val firebaseUser = firebaseAuth.currentUser ?: return Result.success(null)
        return try {
            val user = fetchUserFromFirestore(firebaseUser.uid)
                ?: User(
                    uid = firebaseUser.uid,
                    fullName = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    // ─── Logout ───────────────────────────────────────────────────────────────

    override fun logout() {
        firebaseAuth.signOut()
    }

    // ─── Update Profile ───────────────────────────────────────────────────────

    override suspend fun updateProfile(
        fullName: String,
        university: String,
        avatarUrl: String
    ): Result<User> {
        val firebaseUser = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Người dùng chưa đăng nhập."))
        return try {
            // Cập nhật profile trong Firebase Auth (displayName)
            val profileUpdates = userProfileChangeRequest {
                displayName = fullName
            }
            firebaseUser.updateProfile(profileUpdates).await()

            // Cập nhật trong Firestore
            val userRef = firestore.collection(USERS_COLLECTION).document(firebaseUser.uid)
            val updates = hashMapOf<String, Any>(
                "fullName" to fullName,
                "university" to university,
                "avatarUrl" to avatarUrl
            )
            userRef.update(updates).await()

            val updatedUser = User(
                uid = firebaseUser.uid,
                fullName = fullName,
                email = firebaseUser.email ?: "",
                university = university,
                avatarUrl = avatarUrl
            )
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    // ─── Credits & Uploads ────────────────────────────────────────────────────

    override suspend fun addCredits(amount: Int): Result<User> {
        val firebaseUser = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Người dùng chưa đăng nhập."))
        return try {
            val userRef = firestore.collection(USERS_COLLECTION).document(firebaseUser.uid)
            
            // Đọc số credits hiện tại
            val snapshot = userRef.get().await()
            val currentCredits = snapshot.getLong("documentCredits")?.toInt() ?: 1
            val newCredits = currentCredits + amount
            
            userRef.update("documentCredits", newCredits).await()
            
            val updatedUser = fetchUserFromFirestore(firebaseUser.uid)
                ?: return Result.failure(Exception("Không thể cập nhật thông tin người dùng."))
            
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    override suspend fun consumeCredit(): Result<User> {
        val firebaseUser = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Người dùng chưa đăng nhập."))
        return try {
            val userRef = firestore.collection(USERS_COLLECTION).document(firebaseUser.uid)
            
            // Đọc số credits hiện tại
            val snapshot = userRef.get().await()
            val currentCredits = snapshot.getLong("documentCredits")?.toInt() ?: 1
            
            if (currentCredits <= 0) {
                return Result.failure(Exception("Bạn đã hết lượt sử dụng tài liệu. Hãy chia sẻ 1 tài liệu học tập của mình để nhận thêm lượt!"))
            }
            
            val newCredits = currentCredits - 1
            userRef.update("documentCredits", newCredits).await()
            
            val updatedUser = fetchUserFromFirestore(firebaseUser.uid)
                ?: return Result.failure(Exception("Không thể cập nhật thông tin người dùng."))
            
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    override suspend fun uploadUserDocument(
        title: String,
        subjectCode: String,
        docType: String,
        fileUri: String
    ): Result<Unit> {
        val firebaseUser = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Người dùng chưa đăng nhập."))
        return try {
            // Lấy thông tin user hiện tại để tự động gán trường đại học
            val user = fetchUserFromFirestore(firebaseUser.uid)
                ?: return Result.failure(Exception("Không thể lấy thông tin người dùng hiện tại."))
            
            // Tạo tài liệu mới trong Firestore collection "documents"
            val docId = firestore.collection("documents").document().id
            val docMap = hashMapOf(
                "id" to docId,
                "courseCode" to subjectCode.trim().uppercase(),
                "title" to title.trim(),
                "type" to docType,
                "quantityLabel" to "Tài liệu tự đăng",
                "rating" to 5.0f,
                "views" to "0",
                "school" to user.university, // Tự động gán trường học của người dùng
                "authorId" to firebaseUser.uid,
                "fileUri" to fileUri,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("documents").document(docId).set(docMap).await()
            
            // Tăng số lượng tài liệu đã đăng của người dùng (+1 uploadCount, +1 documentCredits)
            val userRef = firestore.collection(USERS_COLLECTION).document(firebaseUser.uid)
            val newUploadCount = user.uploadCount + 1
            val newCredits = user.documentCredits + 1
            
            userRef.update(
                mapOf(
                    "uploadCount" to newUploadCount,
                    "documentCredits" to newCredits
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    override suspend fun getDocuments(): Result<List<com.example.eduvault.feature.library.ui.LibraryDoc>> {
        return try {
            val snapshot = firestore.collection("documents").get().await()
            val list = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: return@mapNotNull null
                val courseCode = doc.getString("courseCode") ?: ""
                val title = doc.getString("title") ?: ""
                val typeStr = doc.getString("type") ?: "Ghi chú"
                val type = when (typeStr.lowercase()) {
                    "quiz", "bộ đề", "trắc nghiệm" -> com.example.eduvault.feature.library.ui.LibraryDocType.QUIZ
                    "slide", "bài giảng" -> com.example.eduvault.feature.library.ui.LibraryDocType.SLIDE
                    "tóm tắt", "summary" -> com.example.eduvault.feature.library.ui.LibraryDocType.SUMMARY
                    else -> com.example.eduvault.feature.library.ui.LibraryDocType.NOTE
                }
                val quantityLabel = doc.getString("quantityLabel") ?: "Tài liệu tự đăng"
                val rating = doc.getDouble("rating")?.toFloat() ?: 5.0f
                val views = doc.getString("views") ?: "0"
                
                com.example.eduvault.feature.library.ui.LibraryDoc(
                    id = id,
                    courseCode = courseCode,
                    title = title,
                    type = type,
                    quantityLabel = quantityLabel,
                    rating = rating,
                    views = views,
                    bgIndex = Math.abs(title.hashCode() % 6)
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────


    /**
     * Lưu [User] vào Firestore collection "users".
     * Document ID = UID của Firebase Auth.
     */
    private suspend fun saveUserToFirestore(user: User) {
        val userMap = hashMapOf(
            "uid" to user.uid,
            "fullName" to user.fullName,
            "email" to user.email,
            "university" to user.university,
            "avatarUrl" to user.avatarUrl,
            "documentCredits" to user.documentCredits,
            "uploadCount" to user.uploadCount,
            "createdAt" to user.createdAt,
        )
        firestore
            .collection(USERS_COLLECTION)
            .document(user.uid)
            .set(userMap)
            .await()
    }

    /**
     * Lấy [User] từ Firestore theo UID.
     * Trả về null nếu document không tồn tại.
     */
    private suspend fun fetchUserFromFirestore(uid: String): User? {
        val snapshot = firestore
            .collection(USERS_COLLECTION)
            .document(uid)
            .get()
            .await()

        if (!snapshot.exists()) return null

        return User(
            uid = snapshot.getString("uid") ?: uid,
            fullName = snapshot.getString("fullName") ?: "",
            email = snapshot.getString("email") ?: "",
            university = snapshot.getString("university") ?: "",
            avatarUrl = snapshot.getString("avatarUrl") ?: "",
            documentCredits = snapshot.getLong("documentCredits")?.toInt() ?: 1,
            uploadCount = snapshot.getLong("uploadCount")?.toInt() ?: 0,
            createdAt = snapshot.getLong("createdAt") ?: 0L,
        )
    }

    /**
     * Map Firebase error messages sang tiếng Việt thân thiện với người dùng.
     */
    private fun mapFirebaseError(message: String?): String {
        return when {
            message == null -> "Đã xảy ra lỗi. Vui lòng thử lại."
            message.contains("network", ignoreCase = true) ||
                    message.contains("NETWORK", ignoreCase = true) ->
                "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet."
            message.contains("too-many-requests", ignoreCase = true) ||
                    message.contains("TOO_MANY_ATTEMPTS", ignoreCase = true) ->
                "Quá nhiều lần thử. Vui lòng đợi vài phút rồi thử lại."
            message.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
                "Email hoặc mật khẩu không đúng."
            else -> "Đã xảy ra lỗi. Vui lòng thử lại."
        }
    }
}
