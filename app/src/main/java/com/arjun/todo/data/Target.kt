package com.arjun.todo.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arjun.todo.util.convertMillisToSecs
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import kotlin.math.max
import kotlin.math.min

@Entity(tableName = "target_table")
@Parcelize
data class Target(
    val name: String,
    val targetAmount: Int,
    val progress: Int = 0,
    val period: String = TargetPeriod.WEEKLY.name,
    val beginTimestamp: Long = -1,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)

    val targetHours: Int
        get() = targetMins / 60

    val targetMins: Int
        get() = targetAmount / 60

    val currentProgress: Int
        get() = if (isInProgress) progress + convertMillisToSecs(System.currentTimeMillis() - beginTimestamp) else progress

    val progressPercent: Int
        get() = min(100, 100 * currentProgress / targetAmount)

    val remainingAmount: Int
        get() = max(0, targetAmount - currentProgress)

    val isInProgress: Boolean
        get() = beginTimestamp > 0

    val isDone: Boolean
        get() = currentProgress >= targetAmount
}

enum class TargetPeriod {
    WEEKLY,
    DAILY
}