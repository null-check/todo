package com.arjun.todo.time.alarm

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import com.arjun.todo.data.Target
import com.arjun.todo.util.TARGET_NAME
import com.arjun.todo.util.convertSecsToMillis
import javax.inject.Inject


class AlarmBuilder @Inject constructor(
    private val app: Application
) {

    private val alarmManager: AlarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var pendingIntent: PendingIntent? = null

    fun setTargetFinishAlarm(target: Target, context: Context? = app) {
        val alarmIntent = Intent(context, TargetFinishAlarmReceiver::class.java).apply {
            putExtra(TARGET_NAME, target.name)
        }

        // Only one alarm should be running at a time
        pendingIntent?.let { alarmManager.cancel(pendingIntent) }

        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
        val triggerTime = SystemClock.elapsedRealtime() + convertSecsToMillis(target.remainingAmount)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelLastAlarm() {
        pendingIntent?.let { alarmManager.cancel(pendingIntent) }
    }
}