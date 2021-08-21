package com.amegane3231.imagenotification.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.AppLaunchState
import com.amegane3231.imagenotification.data.NotificationState
import com.amegane3231.imagenotification.data.SharedPreferenceKey
import com.amegane3231.imagenotification.extensions.rgbToGray
import com.amegane3231.imagenotification.service.ForeGroundService
import com.amegane3231.imagenotification.ui.theme.ImageNotificationTheme
import com.amegane3231.imagenotification.viewmodels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by lazy { HomeViewModel() }
    private var isNotifying = false
    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                val uri = it.data?.data!!
                val notificationState = NotificationState.PIN_IMAGE
                val date = Date()
                val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val imageFileName = "image_${formatter.format(date)}.png"
                val iconFileName = "icon_${formatter.format(date)}.png"
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putString(SharedPreferenceKey.ImageFileName.name, imageFileName)
                    putString(SharedPreferenceKey.IconFileName.name, iconFileName)
                }
                val iconImage = getBitmap(uri)
                saveImageFile(iconImage, imageFileName)
                saveIconFile(iconImage, iconFileName)
                isNotifying = true
                homeViewModel.apply {
                    setImage(iconImage.asImageBitmap())
                    changeFileName(iconFileName)
                    changeText(isNotifying)
                }
                startService(iconFileName, notificationState)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appLaunchedState = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getInt(
                SharedPreferenceKey.AppLaunchedState.name,
                AppLaunchState.FirstChoiceImage.state
            )
        if (appLaunchedState <= 1) {
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                putInt(
                    SharedPreferenceKey.AppLaunchedState.name,
                    AppLaunchState.FirstChoiceImage.state
                )
            }
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/png"
            }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                getImageContent.launch(intent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val iconFileName = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(SharedPreferenceKey.IconFileName.name, "")
        iconFileName?.let {
            homeViewModel.changeFileName(it)
            startService(it, NotificationState.PIN_IMAGE)
            isNotifying = true
        }
        val imageFileName = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(SharedPreferenceKey.ImageFileName.name, "")
        imageFileName?.let {
            homeViewModel.setImage(getBitmap(it).asImageBitmap())
        }
        homeViewModel.changeText(isNotifying)
        return ComposeView(inflater.context).apply {
            setContent {
                LayoutContent()
            }
        }
    }

    private fun startService(fileName: String, notificationState: NotificationState) {
        val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
        coroutineScope.launch {
            val intent = Intent(requireContext(), ForeGroundService::class.java).apply {
                putExtra("fileName", fileName)
                putExtra("notificationState", notificationState)
            }
            requireContext().startService(intent)
        }
    }

    private fun startService(notificationState: NotificationState) {
        val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
        coroutineScope.launch {
            val intent = Intent(requireContext(), ForeGroundService::class.java).apply {
                putExtra("notificationState", notificationState)
            }
            requireContext().startService(intent)
        }
    }

    private fun getBitmap(uri: Uri): Bitmap {
        return try {
            val openFileDescriptor =
                requireContext().contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = openFileDescriptor?.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            openFileDescriptor?.close()
            bitmap
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            createBitmap(
                width = DEFAULT_IMAGE_WIDTH,
                height = DEFAULT_IMAGE_HEIGHT,
                config = Bitmap.Config.ARGB_8888
            )
        }
    }

    private fun getBitmap(fileName: String): Bitmap {
        return try {
            requireContext().openFileInput(fileName).use { stream ->
                return BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            createBitmap(
                width = DEFAULT_IMAGE_WIDTH,
                height = DEFAULT_IMAGE_HEIGHT,
                config = Bitmap.Config.ARGB_8888
            )
        }
    }

    private fun saveImageFile(bitmap: Bitmap, fileName: String) {
        requireContext().openFileOutput(fileName, Context.MODE_PRIVATE).use {
            val image = Bitmap.createBitmap(bitmap)
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    private fun saveIconFile(bitmap: Bitmap, fileName: String) {
        requireContext().openFileOutput(fileName, Context.MODE_PRIVATE).use {
            val bitmapInstance = Bitmap.createBitmap(bitmap)
            val iconImage = bitmapInstance.rgbToGray()
            iconImage.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Composable
    fun LayoutContent() {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageState by homeViewModel.imageState.observeAsState()
            Image(
                bitmap = imageState ?: createBitmap(
                    width = DEFAULT_IMAGE_WIDTH,
                    height = DEFAULT_IMAGE_HEIGHT,
                    config = Bitmap.Config.ARGB_8888
                ).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(DEFAULT_IMAGE_WIDTH.dp, DEFAULT_IMAGE_HEIGHT.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "image/png"
                        }
                        if (intent.resolveActivity(requireContext().packageManager) != null) {
                            getImageContent.launch(intent)
                        }
                    },
            )

            val notificationState by homeViewModel.notificationState.observeAsState()
            notificationState?.let {
                OutlinedButton(
                    onClick = {
                        isNotifying = !isNotifying
                        homeViewModel.changeText(isNotifying)
                        if (isNotifying) {
                            val imageFileName =
                                homeViewModel.fileNameLiveData.value
                            imageFileName?.let { fileName ->
                                startService(fileName, NotificationState.PIN_IMAGE)
                            }
                        } else {
                            startService(NotificationState.CANCEL_IMAGE)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BUTTON_HEIGHT)
                        .padding(top = BUTTON_PADDING, start = BUTTON_PADDING, end = BUTTON_PADDING),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = if (isNotifying) ImageNotificationTheme.colors.primary else ImageNotificationTheme.colors.disable,
                        contentColor = ImageNotificationTheme.colors.text
                    )
                ) {
                    Row {
                        val buttonIconDrawable = if (isNotifying) {
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_pin,
                                null
                            ) as VectorDrawable
                        } else {
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_pin_not,
                                null
                            ) as VectorDrawable
                        }
                        val bitmap = Bitmap.createBitmap(
                            buttonIconDrawable.intrinsicWidth,
                            buttonIconDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        buttonIconDrawable.setBounds(0, 0, canvas.width, canvas.height)
                        buttonIconDrawable.draw(canvas)
                        Icon(bitmap = bitmap.asImageBitmap(), contentDescription = null)
                        Text(
                            text = it.getString(requireContext()),
                            modifier = Modifier.padding(top = TEXT_PADDING)
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 512
        private const val DEFAULT_IMAGE_HEIGHT = 512
        private val TEXT_PADDING = 2.dp
        private val BUTTON_HEIGHT = 80.dp
        private val BUTTON_PADDING = 24.dp
    }
}