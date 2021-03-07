package com.arjun.todo.time.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.arjun.todo.R
import com.arjun.todo.util.CHANNEL_ID_TARGET_FINISHED
import com.arjun.todo.util.NOTIFICATION_ID_TARGET_FINISHED
import com.arjun.todo.util.TARGET_NAME
import com.arjun.todo.views.MainActivity

class TargetFinishAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val target = intent.extras?.get(TARGET_NAME) as String

        val actionIntent = Intent(context, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, actionIntent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_TARGET_FINISHED)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO replace with proper icon
            .setContentTitle(context.getString(R.string.title_target_finished, target))
            .setContentText(context.getString(R.string.text_target_finished, target))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_TARGET_FINISHED, builder.build())
        }
    }
}