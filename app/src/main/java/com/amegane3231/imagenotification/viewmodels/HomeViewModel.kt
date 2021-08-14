package com.amegane3231.imagenotification.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private var _imageState = MutableLiveData<ImageBitmap>()
    val imageState: LiveData<ImageBitmap>
        get() = _imageState
    private var _notificationState = MutableLiveData<String>()
    val notificationState: LiveData<String>
        get() = _notificationState

    fun setImage(imageBitmap: ImageBitmap) {
        _imageState.value = imageBitmap
    }

    fun changeText(isNotifying: Boolean) {
        _notificationState.value = if (isNotifying) {
            "通知欄への表示を解除"
        } else {
            "通知欄に表示"
        }
    }
}