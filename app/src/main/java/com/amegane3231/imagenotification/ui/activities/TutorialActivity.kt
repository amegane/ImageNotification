package com.amegane3231.imagenotification.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.preference.PreferenceManager
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.AppLaunchState
import com.amegane3231.imagenotification.data.SharedPreferenceKey
import com.amegane3231.imagenotification.ui.compose.Indicators
import com.amegane3231.imagenotification.ui.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlin.math.abs

class TutorialActivity : ComponentActivity() {
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageList = mutableListOf<Bitmap>().apply {
            add(
                ResourcesCompat.getDrawable(resources, R.drawable.description_image, null)!!
                    .toBitmap(720, 1280, null)
            )
            add(
                ResourcesCompat.getDrawable(resources, R.drawable.description_image_white, null)!!
                    .toBitmap(720, 1280, null)
            )
            add(
                ResourcesCompat.getDrawable(resources, R.drawable.smartphone_people, null)!!
                    .toBitmap(720, 1280, null)
            )
        }


        val textList = mutableListOf<String>()
        textList.apply {
            add(getString(R.string.text_app_description))
            add(getString(R.string.text_app_description2))
            add(getString(R.string.text_app_description3))
        }

        setContent {
            val pagerState = rememberPagerState(pageCount = 3)
            ImageNotificationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                ) {
                    TutorialViewPager(
                        pagerState = pagerState,
                        imageList = imageList,
                        textList = textList
                    )
                }
            }
        }
    }

    @ExperimentalPagerApi
    private fun setPagerBackgroundColor(
        pagerState: PagerState,
        leftColor: Int,
        centerColor: Int,
        rightColor: Int
    ): Color {
        return if (pagerState.isScrollInProgress) {
            if (pagerState.currentPageOffset >= 0) {
                Color(
                    ColorUtils.blendARGB(
                        centerColor,
                        rightColor,
                        pagerState.currentPageOffset)
                )
            } else {
                Color(
                    ColorUtils.blendARGB(
                        centerColor,
                        leftColor,
                        abs(pagerState.currentPageOffset)
                    )
                )
            }
        } else {
            Color(centerColor)
        }
    }

    @ExperimentalPagerApi
    @Composable
    private fun TutorialViewPager(
        pagerState: PagerState,
        imageList: List<Bitmap>,
        textList: List<String>
    ) {
        val animatedColor = animateColorAsState(
            when (pagerState.currentPage) {
                0 -> setPagerBackgroundColor(
                    pagerState,
                    LightBlue400.toArgb(),
                    LightBlue400.toArgb(),
                    Green400.toArgb()
                )
                1 -> setPagerBackgroundColor(
                    pagerState,
                    LightBlue400.toArgb(),
                    Green400.toArgb(),
                    Orange400.toArgb()
                )
                else -> setPagerBackgroundColor(
                    pagerState,
                    Green400.toArgb(),
                    Orange400.toArgb(),
                    Orange400.toArgb()
                )
            }
        )
        Column(
            modifier = Modifier.background(animatedColor.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(state = pagerState) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        bitmap = imageList[it].asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = textList[it],
                        modifier = Modifier.padding(48.dp),
                        color = ImageNotificationTheme.colors.text,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Indicators(
                currentPosition = pagerState.currentPage,
                contentCount = pagerState.pageCount
            )
            val isLastPage = pagerState.currentPage == pagerState.pageCount - 1
            val buttonAlpha = if (isLastPage) 1F else 0F
            OutlinedButton(
                onClick = {
                    PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        .edit {
                            putInt(
                                SharedPreferenceKey.AppLaunchedState.name,
                                AppLaunchState.NotSetImage.state
                            )
                        }
                    val intent = Intent(this@TutorialActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp)
                    .alpha(buttonAlpha),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = White,
                    contentColor = animatedColor.value
                ),
                enabled = isLastPage
            ) {
                Text(text = getString(R.string.button_start))
            }

        }
    }
}