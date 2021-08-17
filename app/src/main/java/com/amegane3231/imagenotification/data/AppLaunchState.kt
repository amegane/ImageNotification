package com.amegane3231.imagenotification.data

enum class AppLaunchState(val state: Int) {
    InitialLaunch(0),
    NotSetImage(1),
    FirstChoiceImage(2)
}