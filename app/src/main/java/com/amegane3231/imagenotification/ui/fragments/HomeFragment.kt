package com.amegane3231.imagenotification.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.NotificationState
import com.amegane3231.imagenotification.data.SharedPreferenceKey
import com.amegane3231.imagenotification.interfaces.ImageProcessingListener
import com.amegane3231.imagenotification.service.ForeGroundService
import com.amegane3231.imagenotification.viewmodels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(), ImageProcessingListener {
    private val homeViewModel: HomeViewModel by lazy { HomeViewModel() }
    private var imageUri: Uri? = null
    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                val uri = it.data?.data!!
                val notificationState = if (this.imageUri != null) {
                    NotificationState.CHANGE_IMAGE
                } else {
                    NotificationState.PIN_IMAGE
                }
                imageUri = uri
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putString(SharedPreferenceKey.ImageUri.name, uri.toString())
                }
                val iconImage = getBitmap(uri)
                homeViewModel.setImage(iconImage)
                startService(uri, notificationState)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        imageUri = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(SharedPreferenceKey.ImageUri.name, "")?.toUri()
        imageUri?.let {
            homeViewModel.setImage(getBitmap(it))
            startService(it, NotificationState.PIN_IMAGE)
        }
        return ComposeView(inflater.context).apply {
            setContent {
                LayoutContent()
            }
        }
    }

    private fun startService(uri: Uri, notificationState: NotificationState) {
        val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
        coroutineScope.launch {
            val intent = Intent(requireContext(), ForeGroundService::class.java).apply {
                putExtra("imageUri", uri.toString())
                putExtra("notificationState", notificationState)
            }
            requireContext().startForegroundService(intent)
        }
    }

    private fun startService(notificationState: NotificationState) {
        val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
        coroutineScope.launch {
            val intent = Intent(requireContext(), ForeGroundService::class.java).apply {
                putExtra("notificationState", notificationState)
            }
            requireContext().startForegroundService(intent)
        }
    }

    private fun getBitmap(uri: Uri): ImageBitmap {
        return try {
            val openFileDescriptor =
                requireContext().contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = openFileDescriptor?.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            openFileDescriptor?.close()
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            createBitmap(
                width = DEFAULT_IMAGE_WIDTH,
                height = DEFAULT_IMAGE_HEIGHT,
                config = Bitmap.Config.ARGB_8888
            ).asImageBitmap()
        }
    }

    @Preview
    @Composable
    fun LayoutContent() {
        Column(verticalArrangement = Arrangement.Center) {
            val imageState by homeViewModel.imageState.observeAsState()
            imageState?.let { ImageContent(image = it) }
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/png"
                    }
                    if (intent.resolveActivity(requireContext().packageManager) != null) {
                        getImageContent.launch(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PADDING_BUTTON)
            ) {
                Text(text = getString(R.string.button_change_image))
            }
            Button(
                onClick = {
                    startService(NotificationState.CANCEL_IMAGE)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PADDING_BUTTON)
            ) {
                Text(text = "通知欄への表示を解除")
            }
        }
    }

    @Composable
    private fun ImageContent(image: ImageBitmap) {
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = Modifier.size(DEFAULT_IMAGE_WIDTH.dp, DEFAULT_IMAGE_HEIGHT.dp),
        )
    }

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 512
        private const val DEFAULT_IMAGE_HEIGHT = 512
        private val MARGIN_BUTTON = 12.dp
        private val PADDING_BUTTON = 12.dp
    }
}