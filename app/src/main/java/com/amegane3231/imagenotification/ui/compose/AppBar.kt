package com.amegane3231.imagenotification.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amegane3231.imagenotification.ui.theme.ImageNotificationTheme

@Composable
fun AppBar(title: String) {
    TopAppBar(
        backgroundColor = ImageNotificationTheme.colors.primary,
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(Modifier.height(32.dp)) {
            Row(
                Modifier
                    .fillMaxHeight()
                    .width(72.dp - 4.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.high,
                ) {
                    IconButton(
                        onClick = { },
                        enabled = true,
                    ) {
                        Icon(
                            painter = painterResource(id = com.amegane3231.imagenotification.R.drawable.image_notification),
                            contentDescription = null,
                        )
                    }
                }
            }

            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.h6) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            text = title,
                            color = ImageNotificationTheme.colors.text
                        )
                    }
                }
            }
        }
    }
}