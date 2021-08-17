package com.amegane3231.imagenotification.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.preference.PreferenceManager
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.AppLaunchState
import com.amegane3231.imagenotification.data.SharedPreferenceKey
import com.amegane3231.imagenotification.ui.activities.ui.theme.ImageNotificationTheme
import com.amegane3231.imagenotification.ui.theme.LightBlue400
import com.amegane3231.imagenotification.ui.theme.WhiteDark
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    HorizontalPager(state = pagerState) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
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
                                modifier = Modifier.padding(48.dp)
                            )
                        }
                    }
                    Indicators(
                        currentPosition = pagerState.currentPage,
                        contentCount = pagerState.pageCount
                    )
                    if (pagerState.currentPage == pagerState.pageCount - 1) {
                        Button(onClick = {
                            PreferenceManager.getDefaultSharedPreferences(applicationContext).edit {
                                putInt(SharedPreferenceKey.AppLaunchedState.name, AppLaunchState.NotSetImage.state)
                            }
                            val intent = Intent(this@TutorialActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }, modifier = Modifier.padding(top = 12.dp)) {
                            Text(text = getString(R.string.button_start))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Indicators(currentPosition: Int, contentCount: Int) {
        Row(
            modifier = Modifier.width((12 * contentCount).dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            repeat(contentCount) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(shape = CircleShape)
                        .background(color = if (currentPosition == it) LightBlue400 else WhiteDark),
                )
            }
        }
    }
}