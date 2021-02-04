package com.arjun.todo.util

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
