package com.example.taxidrivermobile.data.repository

import android.util.Log
import com.example.taxidrivermobile.data.api.ApiService
import com.example.taxidrivermobile.data.local.TokenManager
import com.example.taxidrivermobile.data.model.Trip
import com.example.taxidrivermobile.data.model.TripsInfoResponse
import kotlinx.coroutines.flow.first

class TripRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    private suspend fun getAuthToken(): String {
        val token = tokenManager.getToken().first()
        return "Bearer $token"
    }

    // Aktif trip'i getir (assigned veya accepted)
    suspend fun getActiveTrip(): Result<Trip?> {
        return try {
            val token = getAuthToken()

            // 1. Assigned istek at
            val response = apiService.getActiveTrip("assigned", token)

            Log.e("ACTIVE_TRIP_DEBUG", "Assigned Response RAW = $response")

            if (response.success && response.data != null) {
                Log.e("ACTIVE_TRIP_DEBUG", "Assigned Trip FOUND: ${response.data}")
                return Result.success(response.data)
            }

            // 2. Assigned yok → Accepted kontrol
            val acceptedResponse = apiService.getActiveTrip("accepted", token)

            Log.e("ACTIVE_TRIP_DEBUG", "Accepted Response RAW = $acceptedResponse")

            if (acceptedResponse.success && acceptedResponse.data != null) {
                Log.e("ACTIVE_TRIP_DEBUG", "Accepted Trip FOUND: ${acceptedResponse.data}")
                return Result.success(acceptedResponse.data)
            }

            // 3. Hiçbiri yok
            Log.e("ACTIVE_TRIP_DEBUG", "No active trip found")
            Result.success(null)

        } catch (e: Exception) {
            Log.e("ACTIVE_TRIP_DEBUG", "ERROR: ", e)
            Result.failure(e)
        }
    }


    // Trip'i kabul et
    suspend fun acceptTrip(tripId: String): Result<Trip> {
        return try {
            val token = getAuthToken()
            val response = apiService.acceptTrip(tripId, token)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Accept failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Trip'i reddet
    suspend fun rejectTrip(tripId: String, reason: String = "Driver declined"): Result<Trip?> {
        return try {
            val token = getAuthToken()
            val response = apiService.rejectTrip(
                tripId,
                mapOf("reason" to reason),
                token
            )

            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Trip'i başlat
    suspend fun startTrip(tripId: String): Result<Trip> {
        return try {
            val token = getAuthToken()
            val response = apiService.startTrip(tripId, token)

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Start failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Trip'i tamamla
    suspend fun completeTrip(tripId: String, actualFare: Double): Result<Trip> {
        return try {
            val token = getAuthToken()
            val response = apiService.completeTrip(
                tripId,
                mapOf("actualFare" to actualFare),
                token
            )

            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Complete failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDriverTrips(status: String?, limit: Int, offset: Int): Result<TripsInfoResponse> {
        return try {
            val token = getAuthToken()
            val response = apiService.getDriverTrips(token, status, limit, offset)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}