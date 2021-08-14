package com.amegane3231.imagenotification.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.*
import androidx.core.graphics.drawable.IconCompat
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.NotificationState
import com.amegane3231.imagenotification.interfaces.ImageProcessingListener
import java.io.IOException

class ForeGroundService : Service(), ImageProcessingListener {
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

            val fileName = intent?.getStringExtra("fileName")
            fileName?.let {
                try {
                    applicationContext.openFileInput(it).use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val bitmapInstance = Bitmap.createBitmap(bitmap)
                        val grayImage = rgbToGray(bitmapInstance)
                        val iconImage = createAlphaImage(grayImage)
                        setSmallIcon(IconCompat.createFromIcon(this@ForeGroundService, Icon.createWithAdaptiveBitmap(iconImage))!!)
                    }
                } catch (e: Exception) {
                    Log.e("Exception", e.toString())
                }
            }
        }.build()
        notification.flags = Notification.FLAG_ONGOING_EVENT

        val notificationState = intent?.getSerializableExtra("notificationState") as NotificationState
        when (notificationState) {
            NotificationState.PIN_IMAGE -> {
                startForeground(NOTIFICATION_ID, notification)
            }
            NotificationState.CANCEL_IMAGE -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        }
        return START_STICKY
    }

    private fun getBitmap(uri: Uri): Bitmap? {
        return try {
            val bitmap =
                applicationContext.contentResolver.openFileDescriptor(uri, "r").use {
                    BitmapFactory.decodeFileDescriptor(it?.fileDescriptor)
                }
            bitmap
        } catch (e: IOException) {
            return null
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "IMAGE_NOTIFICATION"
    }
}