package com.runiq.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.runiq.R
import com.runiq.domain.manager.LocationManager
import com.runiq.domain.repository.RunRepository
import com.runiq.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackingService : LifecycleService() {
    
    @Inject
    lateinit var locationManager: LocationManager
    
    @Inject
    lateinit var runRepository: RunRepository
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_tracking"
        private const val CHANNEL_NAME = "Location Tracking"
        
        fun startService(context: Context) {
            val intent = Intent(context, LocationTrackingService::class.java)
            context.startForegroundService(intent)
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, LocationTrackingService::class.java)
            context.stopService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("LocationTrackingService created with injected dependencies")
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationTracking()
    }
    
    private fun startLocationTracking() {
        locationManager.getLocationUpdates()
            .onEach { location ->
                Timber.d("Location update: ${location.latitude}, ${location.longitude}")
                // Update current run with location data
                runRepository.updateCurrentRunLocation(location)
            }
            .launchIn(lifecycleScope)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks your location during runs"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RunIQ - Tracking your run")
            .setContentText("Location tracking is active")
            .setSmallIcon(R.drawable.ic_run) // You'll need to add this icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("LocationTrackingService destroyed")
    }
}