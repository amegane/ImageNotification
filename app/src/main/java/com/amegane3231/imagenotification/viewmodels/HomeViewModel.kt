package com.amegane3231.imagenotification.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.StringResource

class HomeViewModel : ViewModel() {
    private var _imageState = MutableLiveData<ImageBitmap>()
    val imageState: LiveData<ImageBitmap>
        get() = _imageState
    private var _notificationState = MutableLiveData<StringResource>()
    val notificationState: LiveData<StringResource>
        get() = _notificationState

    fun setImage(imageBitmap: ImageBitmap) {
        _imageState.value = imageBitmap
    }

    fun changeText(isNotifying: Boolean) {
        _notificationState.value = if (isNotifying) {
            StringResource.create(R.string.text_release_notification)
        } else {
            StringResource.create(R.string.text_display_notification)
        }
    }
}