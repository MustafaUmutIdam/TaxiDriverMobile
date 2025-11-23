package com.example.taxidrivermobile.data.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.example.taxidrivermobile.R
import com.example.taxidrivermobile.data.api.RetrofitClient
import com.example.taxidrivermobile.data.local.TokenManager
import com.example.taxidrivermobile.data.model.UpdateLocationRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "LocationServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Konum Servisi",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Taksi Şoför")
        .setContentText("Konum izleniyor...")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .build()

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            30000L // 30 saniye
        ).apply {
            setMinUpdateIntervalMillis(15000L) // 15 saniye
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    sendLocationToServer(location.latitude, location.longitude)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun sendLocationToServer(lat: Double, lng: Double) {
        serviceScope.launch {
            try {
                val tokenManager = TokenManager(applicationContext)
                val token = tokenManager.getToken().first()

                if (token != null) {
                    // Driver ID'yi token'dan parse et (basit yöntem)
                    // Gerçek uygulamada Driver ID'yi SharedPreferences'ta sakla
                    val response = RetrofitClient.apiService.updateLocation(
                        driverId = "DRIVER_ID", // TODO: Gerçek driver ID kullan
                        request = UpdateLocationRequest(lat, lng),
                        token = "Bearer $token"
                    )
                    println("Location updated: $lat, $lng")
                }
            } catch (e: Exception) {
                println("Location update failed: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}