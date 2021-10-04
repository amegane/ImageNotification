package com.amegane3231.imagenotification.viewmodels

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.StringResource
import com.amegane3231.imagenotification.extensions.rgbToGray

class HomeViewModel : ViewModel() {
    private var _imageState = MutableLiveData<ImageBitmap>()

    val imageState: LiveData<ImageBitmap> = _imageState

    private var _notificationState = MutableLiveData<StringResource>()

    val notificationState: LiveData<StringResource> = _notificationState

    private var _fileNameLiveData = MutableLiveData<String>()

    val fileNameLiveData: LiveData<String> = _fileNameLiveData

    fun setImage(imageBitmap: ImageBitmap) {
        _imageState.value = imageBitmap
    }

    fun changeText(isNotifying: Boolean) {
        _notificationState.value = if (isNotifying) {
            StringResource.create(R.string.text_display_notification)
        } else {
            StringResource.create(R.string.text_release_notification)
        }
    }

    fun changeFileName(fileName: String) {
        _fileNameLiveData.value = fileName
    }

    fun getBitmap(uri: Uri, context: Context, resources: Resources): Bitmap {
        return try {
            val openFileDescriptor =
                context.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = openFileDescriptor?.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            openFileDescriptor?.close()
            bitmap
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            ResourcesCompat.getDrawable(resources, R.drawable.image_notification, null)!!
                .toBitmap(
                    DEFAULT_IMAGE_WIDTH,
                    DEFAULT_IMAGE_HEIGHT,
                    null
                )
        }
    }

    fun getBitmap(fileName: String, context: Context, resources: Resources): Bitmap {
        return try {
            context.openFileInput(fileName).use { stream ->
                return BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            ResourcesCompat.getDrawable(resources, R.drawable.image_notification, null)!!
                .toBitmap(
                    DEFAULT_IMAGE_WIDTH,
                    DEFAULT_IMAGE_HEIGHT,
                    null
                )
        }
    }

    fun saveImageFile(bitmap: Bitmap, fileName: String, context: Context) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            val image = Bitmap.createBitmap(bitmap)
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    fun saveIconFile(bitmap: Bitmap, fileName: String, context: Context) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            val bitmapInstance = Bitmap.createBitmap(bitmap)
            val iconImage = bitmapInstance.rgbToGray()
            iconImage.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 448
        private const val DEFAULT_IMAGE_HEIGHT = 448
    }
}