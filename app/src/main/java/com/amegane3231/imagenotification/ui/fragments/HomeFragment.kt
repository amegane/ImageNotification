package com.amegane3231.imagenotification.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.AppLaunchState
import com.amegane3231.imagenotification.data.NotificationState
import com.amegane3231.imagenotification.data.SharedPreferenceKey
import com.amegane3231.imagenotification.service.ForeGroundService
import com.amegane3231.imagenotification.ui.compose.AppBar
import com.amegane3231.imagenotification.ui.theme.ImageNotificationTheme
import com.amegane3231.imagenotification.viewmodels.HomeViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()

    private var interstitialAd: InterstitialAd? = null

    private var isNotifying = false

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                val uri = it.data?.data!!
                val notificationState = NotificationState.PIN_IMAGE
                val iconImage = homeViewModel.getBitmap(uri, requireContext(), resources)
                attachIconImage(iconImage, notificationState)
                interstitialAd?.show(requireActivity())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appLaunchedState = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getInt(
                SharedPreferenceKey.AppLaunchedState.name,
                AppLaunchState.InitialLaunch.state
            )
        when (appLaunchedState) {
            AppLaunchState.InitialLaunch.state -> {
                findNavController().navigate(R.id.action_home_to_tutorial)
            }
            AppLaunchState.NotSetImage.state -> {
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
            AppLaunchState.FirstChoiceImage.state -> {
            }
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("Failed to load", adError.message)
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("Success", "Ad loaded")
                    this@HomeFragment.interstitialAd = interstitialAd
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setContent {
                val scaffoldState = rememberScaffoldState()
                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = { AppBar(getString(R.string.app_name)) },
                    bottomBar = { AdView() },
                    content = {
                        LayoutContent()
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val iconFileName = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(SharedPreferenceKey.IconFileName.name, null)
        if (!iconFileName.isNullOrBlank()) {
            homeViewModel.changeFileName(iconFileName)
            startService(iconFileName, NotificationState.PIN_IMAGE)
            isNotifying = true
        } else {
            val iconImage =
                ResourcesCompat
                    .getDrawable(resources, R.drawable.image_notification, null)!!
                    .toBitmap(
                        DEFAULT_IMAGE_WIDTH,
                        DEFAULT_IMAGE_HEIGHT,
                        null
                    )
            attachIconImage(iconImage, NotificationState.PIN_IMAGE)
        }

        val imageFileName = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString(SharedPreferenceKey.ImageFileName.name, null)
        if (!imageFileName.isNullOrBlank()) {
            homeViewModel.setImage(
                homeViewModel.getBitmap(
                    imageFileName,
                    requireContext(),
                    resources
                ).asImageBitmap()
            )
        } else {
            homeViewModel.setImage(
                ResourcesCompat
                    .getDrawable(resources, R.drawable.image_notification, null)!!
                    .toBitmap(
                        DEFAULT_IMAGE_WIDTH,
                        DEFAULT_IMAGE_HEIGHT,
                        null
                    ).asImageBitmap()
            )
        }

        homeViewModel.changeText(isNotifying)
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

    private fun attachIconImage(bitmap: Bitmap, notificationState: NotificationState) {
        val date = Date()
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val imageFileName = "image_${formatter.format(date)}.png"
        val iconFileName = "icon_${formatter.format(date)}.png"
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
            putString(SharedPreferenceKey.ImageFileName.name, imageFileName)
            putString(SharedPreferenceKey.IconFileName.name, iconFileName)
        }
        homeViewModel.saveImageFile(bitmap, imageFileName, requireContext())
        homeViewModel.saveIconFile(bitmap, iconFileName, requireContext())
        isNotifying = true
        homeViewModel.apply {
            setImage(bitmap.asImageBitmap())
            changeFileName(iconFileName)
            changeText(isNotifying)
        }
        startService(iconFileName, notificationState)
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
                    DEFAULT_IMAGE_WIDTH,
                    DEFAULT_IMAGE_HEIGHT,
                    Bitmap.Config.ARGB_8888
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
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = if (isNotifying) ImageNotificationTheme.colors.primary else ImageNotificationTheme.colors.disable,
                        contentColor = ImageNotificationTheme.colors.text
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BUTTON_HEIGHT)
                        .padding(
                            top = BUTTON_PADDING,
                            start = BUTTON_PADDING,
                            end = BUTTON_PADDING
                        )
                ) {

                    Row {
                        Icon(
                            painter = painterResource(id = if (isNotifying) R.drawable.ic_pin else R.drawable.ic_pin_not),
                            contentDescription = null,
                            modifier = Modifier.padding(end = BUTTON_ICON_PADDING)
                        )
                        Text(
                            text = it.getString(requireContext()),
                            modifier = Modifier.padding(top = TEXT_PADDING)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AdView() {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                val adView = com.google.android.gms.ads.AdView(context)
                val displayMetrics = Resources.getSystem().displayMetrics
                val width = (displayMetrics.widthPixels / displayMetrics.density).toInt()
                adView.adSize =
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
                adView.adUnitId = BANNER_AD_UNIT_ID
                adView.loadAd(AdRequest.Builder().build())
                adView
            }
        )
    }

    companion object {
        private const val DEFAULT_IMAGE_WIDTH = 448
        private const val DEFAULT_IMAGE_HEIGHT = 448
        private val TEXT_PADDING = 2.dp
        private val BUTTON_HEIGHT = 80.dp
        private val BUTTON_PADDING = 24.dp
        private val BUTTON_ICON_PADDING = 12.dp
        private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    }
}