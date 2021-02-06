package com.arjun.todo.util

import java.util.*

val <T> T.exhaustive: T
    get() = this

fun convertMillisToMins(millis: Long): Int = (millis / 60000).toInt()

fun convertMinsToMillis(mins: Int): Long = (mins * 60000).toLong()

fun getMinsFormatted(mins: Int): String {

    val hours = mins.div(60)
    val minsRem = mins.rem(60)

    val minsString = if (minsRem == 1) "$minsRem Min" else "$minsRem Mins"
    return if (hours > 0) {
        val hoursString = if (hours == 1) "$hours Hour" else "$hours Hours"
        if (minsRem != 0) {
            "$hoursString $minsString"
        } else {
            hoursString
        }
    } else {
        minsString
    }
}

fun getNextResetTime(): Long {
    val currentTime = Calendar.getInstance()
    val dueTime = Calendar.getInstance()
    dueTime.set(Calendar.HOUR_OF_DAY, 0)
    dueTime.set(Calendar.MINUTE, 0)
    dueTime.set(Calendar.SECOND, 0)
    if (dueTime.timeInMillis  - currentTime.timeInMillis < 10000) { // Debounce of 10 seconds
        dueTime.add(Calendar.HOUR_OF_DAY, 24)
    }
    return dueTime.timeInMillis - currentTime.timeInMillis
}
