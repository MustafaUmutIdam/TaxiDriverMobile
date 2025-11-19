package com.example.taxidrivermobile.data.repository

import com.example.taxidrivermobile.data.api.ApiService
import com.example.taxidrivermobile.data.local.TokenManager
import com.example.taxidrivermobile.data.model.*
import kotlinx.coroutines.flow.first

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(phone: String, password: String): Result<AuthData> {
        return try {
            val response = apiService.login(LoginRequest(phone, password))
            if (response.success && response.data != null) {
                tokenManager.saveToken(response.data.token)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthData> {
        return try {
            val response = apiService.register(request)
            if (response.success && response.data != null) {
                tokenManager.saveToken(response.data.token)
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStations(): Result<List<Station>> {
        return try {
            val response = apiService.getStations()
            if (response.success) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to fetch stations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenManager.getToken().first() != null
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }
}