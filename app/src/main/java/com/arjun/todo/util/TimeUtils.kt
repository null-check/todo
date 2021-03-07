package com.arjun.todo.util

import java.util.*

fun convertMillisToSecs(millis: Long): Int = (millis / 1000).toInt()

fun convertMillisToMins(millis: Long): Int = (millis / 60000).toInt()

fun convertSecsToMillis(secs: Int): Long = (secs * 1000).toLong()

/*  Function that takes in seconds remaining, and returns human readable string
*   eg: input, output
*   0, 0 Seconds
*   1, 1 Second
*   5, 5 Seconds
*   60, 1 Min
*   100, 1 Min
*   600, 10 Mins
*   3600, 1 Hour
*   3660, 1 Hour 1 Min
*   7800, 2 Hours 10 Mins
* */
fun getSecsFormatted(secs: Int): String {
    val mins = secs.div(60)

    return if (mins > 0) {
        val hours = mins.div(60)
        val minsRem = mins.rem(60)
        val minsString = if (minsRem == 1) "$minsRem Min" else "$minsRem Mins"

        if (hours > 0) {
            val hoursString = if (hours == 1) "$hours Hour" else "$hours Hours"
            if (minsRem != 0) {
                "$hoursString $minsString"
            } else {
                hoursString
            }
        } else {
            minsString
        }
    } else {
        val secsRem = secs.rem(60)
        if (secsRem == 1) "$secsRem Second" else "$secsRem Seconds"
    }
}

// returns the next timestamp at which to reset all target progress
fun getNextResetTime(currentTime: Calendar): Long {
    val dueTime = Calendar.getInstance()
    dueTime.set(Calendar.HOUR_OF_DAY, 24)
    dueTime.set(Calendar.MINUTE, 0)
    dueTime.set(Calendar.SECOND, 0)

    if (dueTime.timeInMillis  - currentTime.timeInMillis < 10000) {
        // If calculated time is within 10 seconds, schedule it for next day
        dueTime.add(Calendar.HOUR_OF_DAY, 24)
    }
    return dueTime.timeInMillis - currentTime.timeInMillis
}