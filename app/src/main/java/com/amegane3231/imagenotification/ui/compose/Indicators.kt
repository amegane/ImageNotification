package com.amegane3231.imagenotification.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.amegane3231.imagenotification.ui.theme.White
import com.amegane3231.imagenotification.ui.theme.WhiteDark

@Composable
fun Indicators(currentPosition: Int, contentCount: Int) {
    Row(
        modifier = Modifier.width((12 * contentCount).dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        repeat(contentCount) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(shape = CircleShape)
                    .background(color = if (currentPosition == it) White else WhiteDark),
            )
        }
    }
}