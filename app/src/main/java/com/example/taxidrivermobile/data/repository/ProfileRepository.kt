package com.example.taxidrivermobile.data.repository

import android.util.Log
import com.example.taxidrivermobile.data.api.ApiService
import com.example.taxidrivermobile.data.local.TokenManager
import com.example.taxidrivermobile.data.model.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    private suspend fun getAuthToken(): String {
        return tokenManager.getToken().first() ?: ""
    }


    suspend fun getProfile(): Result<Driver> {
        Log.d("ProfileRepository", "Profil getirme işlemi başlatılıyor...")
        return try {
            val token = getAuthToken()
            Log.d("ProfileRepository", "Kullanılan Auth Token: $token")
            val response = apiService.getProfile("Bearer $token")
            Log.d("ProfileRepository", "API Yanıtı: $response")

            if (response.success && response.data != null) {
                Log.d("ProfileRepository", "Profil başarıyla alındı. Sürücü: ${response.data}")
                Result.success(response.data)
            } else {
                val errorMessage = response ?: "Profil bilgileri alınamadı (sunucudan boş veri geldi)."
                Log.e("ProfileRepository", "API yanıtı başarısız veya sürücü verisi null. Mesaj: $errorMessage")
                Result.failure(Exception(errorMessage as String?))
            }
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Profil alınırken istisna oluştu", e)
            Result.failure(e)
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<Unit> {
        return try {
            val token = getAuthToken()
            val response = apiService.updateProfile(request, "Bearer $token")

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Güncelleme başarısız"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val token = getAuthToken()
            val request = ChangePasswordRequest(currentPassword, newPassword)
            val response = apiService.changePassword(request, "Bearer $token")

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Şifre değiştirme başarısız"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(driverId: String, status: String): Result<DriverStatusUpdate> {
        return try {
            val token = getAuthToken()
            val request = UpdateStatusRequest(status)
            val response = apiService.updateStatus(request, "Bearer $token")

            if (response.success && response.data != null) {
                Log.d("ProfileRepository", "Durum başarıyla güncellendi. Yeni durum: ${response.data}")
                Result.success(response.data)

            } else {
                Result.failure(Exception(response.message ?: "Durum güncelleme başarısız"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }
}
