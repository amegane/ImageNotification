package com.amegane3231.imagenotification.ui.fragments

import android.app.Activity
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
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.edit
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.SharedPreferenceKey
import com.amegane3231.imagenotification.interfaces.ImageProcessingListener
import com.amegane3231.imagenotification.service.ForeGroundService
import com.amegane3231.imagenotification.viewmodels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), ImageProcessingListener {
    private val homeViewModel: HomeViewModel by lazy { HomeViewModel() }
    private var imageUri: Uri? = null
    private var isPinned = false
    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                val uri = it.data?.data!!
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putString(SharedPreferenceKey.ImageUri.name, uri.toString())
                }
                val iconImage = getBitmap(uri)
                homeViewModel.setImage(iconImage)
                isPinned = true
                startService(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        imageUri = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(SharedPreferenceKey.ImageUri.name, "")?.toUri()
        return ComposeView(inflater.context).apply {
            setContent {
                ConstraintLayoutContent()
            }
        }
    }

    private fun startService(uri: Uri) {
        val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
        coroutineScope.launch {
            val intent = Intent(requireContext(), ForeGroundService::class.java).apply {
                putExtra("imageUri", uri.toString())
                putExtra("isPinned", isPinned)
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

    @Composable
    fun ConstraintLayoutContent() {
        ConstraintLayout {
            val (image, button) = createRefs()
            if (imageUri != null) {
                val imageBitmap = getBitmap(imageUri!!)
                val imageState by homeViewModel.imageState.observeAsState()
                if (imageState != null) {
                    Image(
                        bitmap = imageState!!,
                        contentDescription = null,
                        modifier = Modifier.constrainAs(image) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        })
                } else {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        modifier = Modifier.constrainAs(image) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        })
                }
                startService(imageUri!!)
            } else {
                val imageState by homeViewModel.imageState.observeAsState()
                imageState?.let { Image(bitmap = it, contentDescription = null) }
            }
            Button(onClick = {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/png"
                }
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    getImageContent.launch(intent)
                }
            }, modifier = Modifier.constrainAs(button) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 12.dp)
            }) {
                Text(text = getString(R.string.button_change_image))
            }
        }
    }

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 512
        private const val DEFAULT_IMAGE_HEIGHT = 512
    }
}
