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
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.amegane3231.imagenotification.data.SharedPreferenceKey
import com.amegane3231.imagenotification.databinding.FragmentHomeBinding
import com.amegane3231.imagenotification.interfaces.ImageProcessingListener
import com.amegane3231.imagenotification.service.ForeGroundService
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), ImageProcessingListener {
    private lateinit var binding: FragmentHomeBinding
    private var isPinned = false
    private val imageContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                val uri = it.data?.data!!
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putString(SharedPreferenceKey.ImageUri.name, uri.toString())
                }
                val iconImage = getBitmap(uri)
                setImage(iconImage, requireContext())
                isPinned = true
                startService(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val imageUri = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(SharedPreferenceKey.ImageUri.name, "")?.toUri()
        imageUri?.let {
            setImage(it, requireContext())
            startService(it)
        }

        binding.buttonSetNotification.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/png"
            }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                imageContent.launch(intent)
            }
        }

        return binding.root
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

    private fun getBitmap(uri: Uri): Bitmap {
        val openFileDescriptor =
            requireContext().contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = openFileDescriptor?.fileDescriptor
        val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        openFileDescriptor?.close()
        return bitmap
    }

    private fun setImage(uri: Uri, context: Context) {
        Glide.with(context).load(uri).into(binding.imageNotification)
    }

    private fun setImage(bitmap: Bitmap, context: Context) {
        Glide.with(context).load(bitmap).into(binding.imageNotification)
    }

    companion object {
        private const val CHANNEL_ID = "7"
    }
}
