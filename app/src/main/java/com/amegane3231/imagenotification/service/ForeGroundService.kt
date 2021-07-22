package com.amegane3231.imagenotification.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.graphics.*
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.interfaces.ImageProcessingListener
import java.io.IOException

class ForeGroundService : Service(), ImageProcessingListener {
    private var hasImage = false
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = getString((R.string.app_name))
        val descriptionText = ""
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = descriptionText
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle(name)
            priority = NotificationCompat.PRIORITY_DEFAULT

            val uri = intent?.getStringExtra("imageUri")?.toUri()
            uri?.let {
                val iconImage = createImageForIcon(uri)
                iconImage?.let {
                    setSmallIcon(IconCompat.createFromIcon(this@ForeGroundService, Icon.createWithAdaptiveBitmap(iconImage))!!)
                    hasImage = true
                }
            }
        }.build()
        notification.flags = Notification.FLAG_ONGOING_EVENT

        if (!hasImage) return START_STICKY
        val isPinned = intent?.getBooleanExtra("isPinned", false) ?: false
        if (isPinned) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    private fun getBitmap(uri: Uri): Bitmap? {
        return try {
            val openFileDescriptor =
                applicationContext.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = openFileDescriptor?.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            openFileDescriptor?.close()
            bitmap
        } catch (e: IOException) {
            return null
        }
    }

    private fun createImageForIcon(uri: Uri): Bitmap? {
        val bitmap = getBitmap(uri)
        bitmap?.let {
            val bitmapInstance = Bitmap.createBitmap(bitmap)
            val grayImage = rgbToGray(bitmapInstance)
            val resizeImage = resize(grayImage, resources.displayMetrics.densityDpi)
            return createAlphaImage(grayImage)
        }
        return null
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "IMAGE_NOTIFICATION"
    }
}