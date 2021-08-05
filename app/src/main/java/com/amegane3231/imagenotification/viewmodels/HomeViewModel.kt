package com.amegane3231.imagenotification.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private var _imageState = MutableLiveData<ImageBitmap>()
    val imageState: LiveData<ImageBitmap>
        get() = _imageState

    fun setImage(imageBitmap: ImageBitmap) {
        _imageState.value = imageBitmap
    }
}