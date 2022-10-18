package com.app.co.event_sharing.data

import android.view.View

data class IAnimate(
    val bind: View,
    val time: Long,
    val delay: Long,
    val done: Boolean?,
)