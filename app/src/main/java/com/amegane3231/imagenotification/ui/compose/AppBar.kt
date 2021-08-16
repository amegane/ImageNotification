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
import com.amegane3231.imagenotification.ui.theme.White

@Composable
fun AppBar(title: String) {
//    TopAppBar(
//        title = { Text(title) },
//        navigationIcon = {
//            IconButton(onClick = { /* do something */ }) {
//                Icon(Icons.Filled.Menu, contentDescription = "Open drawer")
//            }
//        },
//        actions = {
//            IconButton(onClick = { /* do something */ }) {
//                Icon(Icons.Filled.Edit, contentDescription = "Edit text")
//            }
//            IconButton(onClick = { /* do something */ }) {
//                Icon(Icons.Filled.Share, contentDescription = "Share text")
//            }
//        }
//    )
    val titleIconModifier = Modifier.fillMaxHeight()
        .width(72.dp - 4.dp)

    TopAppBar(
        backgroundColor = White,
        elevation = 0.dp,
        modifier= Modifier.fillMaxWidth()) {

        //TopAppBar Content
        Box(Modifier.height(32.dp)) {

            //Navigation Icon
            Row(titleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.high,
                ) {
                    IconButton(
                        onClick = { },
                        enabled = true,
                    ) {
                        Icon(
                            painter = painterResource(id = com.amegane3231.imagenotification.R.drawable.ic_launcher_foreground),
                            contentDescription = "Back",
                        )
                    }
                }
            }

            //Title
            Row(Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically) {

                ProvideTextStyle(value = MaterialTheme.typography.h6) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                    ){
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            text = title,
                        )
                    }
                }
            }

            //actions
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(
                    Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {}
                )
            }
        }
    }
}