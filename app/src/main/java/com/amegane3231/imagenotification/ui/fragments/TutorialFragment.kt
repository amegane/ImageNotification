package com.amegane3231.imagenotification.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.amegane3231.imagenotification.R
import com.amegane3231.imagenotification.data.AppLaunchState
import com.amegane3231.imagenotification.data.SharedPreferenceKey
import com.amegane3231.imagenotification.data.TutorialPageData
import com.amegane3231.imagenotification.ui.compose.Indicators
import com.amegane3231.imagenotification.ui.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlin.math.ceil
import kotlin.math.floor

class TutorialFragment : Fragment() {
    @ExperimentalPagerApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tutorialPages = listOf(
            TutorialPageData(
                ResourcesCompat
                    .getDrawable(resources, R.drawable.description_image, null)!!
                    .toBitmap(PAGER_IMAGE_WIDTH, PAGER_IMAGE_HEIGHT, null),
                getString(R.string.text_app_description),
                LightBlue400
            ),
            TutorialPageData(
                ResourcesCompat
                    .getDrawable(resources, R.drawable.description_image_white, null)!!
                    .toBitmap(PAGER_IMAGE_WIDTH, PAGER_IMAGE_HEIGHT, null),
                getString(R.string.text_app_description2),
                Green400
            ),
            TutorialPageData(
                ResourcesCompat
                    .getDrawable(resources, R.drawable.smartphone_people, null)!!
                    .toBitmap(PAGER_IMAGE_WIDTH, PAGER_IMAGE_HEIGHT, null),
                getString(R.string.text_app_description3),
                Orange400
            )
        )
        return ComposeView(inflater.context).apply {
            setContent {
                val pagerState = rememberPagerState(pageCount = tutorialPages.size)
                TutorialViewPager(
                    pagerState = pagerState,
                    tutorialPages = tutorialPages
                )
            }
        }
    }

    @ExperimentalPagerApi
    private fun setPagerBackgroundColor(
        pagerState: PagerState,
        colorList: List<Color>
    ): Color {
        return if (pagerState.isScrollInProgress) {
            if (pagerState.currentPageOffset >= 0) {
                val currentIndex =
                    pagerState.currentPage + floor(pagerState.currentPageOffset).toInt()
                val nextIndex =
                    if (pagerState.currentPage + floor(pagerState.currentPageOffset).toInt() != pagerState.pageCount - 1) {
                        pagerState.currentPage + floor(pagerState.currentPageOffset).toInt() + 1
                    } else {
                        pagerState.currentPage + floor(pagerState.currentPageOffset).toInt()
                    }

                Color(
                    ColorUtils.blendARGB(
                        colorList[currentIndex].toArgb(),
                        colorList[nextIndex].toArgb(),
                        pagerState.currentPageOffset - floor(pagerState.currentPageOffset)
                    )
                )
            } else {
                val currentIndex =
                    pagerState.currentPage + ceil(pagerState.currentPageOffset).toInt()
                val previousIndex =
                    if (pagerState.currentPage + ceil(pagerState.currentPageOffset).toInt() != 0) {
                        pagerState.currentPage + ceil(pagerState.currentPageOffset).toInt() - 1
                    } else {
                        pagerState.currentPage + ceil(pagerState.currentPageOffset).toInt()
                    }

                Color(
                    ColorUtils.blendARGB(
                        colorList[previousIndex].toArgb(),
                        colorList[currentIndex].toArgb(),
                        pagerState.currentPageOffset - floor(pagerState.currentPageOffset)
                    )
                )
            }
        } else {
            Color(colorList[pagerState.currentPage].toArgb())
        }
    }

    @ExperimentalPagerApi
    @Composable
    private fun TutorialViewPager(
        pagerState: PagerState,
        tutorialPages: List<TutorialPageData>
    ) {
        val animatedColor = animateColorAsState(
            setPagerBackgroundColor(pagerState, tutorialPages.map { it.backgroundColor })
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.background(animatedColor.value)
        ) {
            HorizontalPager(state = pagerState, itemSpacing = PAGER_ITEM_SPACING) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(animatedColor.value)
                        .fillMaxWidth()
                        .padding(top = PAGER_PADDING)
                ) {
                    Image(
                        bitmap = tutorialPages[it].image.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.padding(top = IMAGE_PADDING)
                    )
                    Text(
                        text = tutorialPages[it].text,
                        color = ImageNotificationTheme.colors.text,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            horizontal = TEXT_PADDING_HORIZONTAL,
                            vertical = TEXT_PADDING_VERTICAL
                        )
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
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
                        .edit {
                            putInt(
                                SharedPreferenceKey.AppLaunchedState.name,
                                AppLaunchState.FirstChoiceImage.state
                            )
                        }
                    val action =
                        TutorialFragmentDirections.actionTutorialToHome(AppLaunchState.FirstChoiceImage.state)
                    findNavController().navigate(action)
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = White,
                    contentColor = animatedColor.value
                ),
                enabled = isLastPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BUTTON_HEIGHT)
                    .padding(top = BUTTON_PADDING, start = BUTTON_PADDING, end = BUTTON_PADDING)
                    .alpha(buttonAlpha)
            ) {
                Text(text = getString(R.string.button_start))
            }

        }
    }

    @Composable
    fun PageView(image: Bitmap, text: String) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(top = IMAGE_PADDING)
        )
        Text(
            text = text,
            color = ImageNotificationTheme.colors.text,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = TEXT_PADDING_HORIZONTAL,
                vertical = TEXT_PADDING_VERTICAL
            )
        )
    }

    companion object {
        private const val PAGER_IMAGE_WIDTH = 720
        private const val PAGER_IMAGE_HEIGHT = 1280
        private val PAGER_ITEM_SPACING = 24.dp
        private val PAGER_PADDING = 12.dp
        private val IMAGE_PADDING = 12.dp
        private val TEXT_PADDING_HORIZONTAL = 24.dp
        private val TEXT_PADDING_VERTICAL = 48.dp
        private val BUTTON_HEIGHT = 80.dp
        private val BUTTON_PADDING = 24.dp
    }
}