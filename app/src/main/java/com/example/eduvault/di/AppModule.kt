package com.example.eduvault.di

import com.example.eduvault.data.repository.AuthRepositoryImpl
import com.example.eduvault.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module cung cấp các dependency cho toàn app.
 *
 * Quy tắc: Module này là nơi DUY NHẤT biết về Firebase và các implementation cụ thể.
 * ViewModel và Repository interface không cần biết chúng đến từ đâu.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /**
     * Bind AuthRepositoryImpl vào interface AuthRepository.
     * Hilt sẽ tự inject AuthRepositoryImpl khi có ai cần AuthRepository.
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {

        /**
         * Cung cấp instance FirebaseAuth duy nhất (Singleton).
         */
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        /**
         * Cung cấp instance FirebaseFirestore duy nhất (Singleton).
         */
        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    }
}
