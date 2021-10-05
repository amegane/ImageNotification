package com.amegane3231.imagenotification.ui.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.amegane3231.imagenotification.databinding.ActivityMainBinding
import com.amegane3231.imagenotification.ui.theme.ImageNotificationTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageNotificationTheme {
                AndroidViewBinding(ActivityMainBinding::inflate)
            }
        }
    }
}