package com.arjun.todo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arjun.todo.util.getNextResetTime
import com.arjun.todo.time.workers.TargetResetWorker
import com.arjun.todo.util.CHANNEL_ID_TARGET_FINISHED
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "TodoApplication"

@HiltAndroidApp
class TodoApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerConfiguration: Configuration

    override fun getWorkManagerConfiguration(): Configuration {
        return workerConfiguration
    }

    override fun onCreate() {
        super.onCreate()
        setupWorkManager()
        createNotificationChannels()
    }

    private fun setupWorkManager() {
        val workManager = WorkManager.getInstance(applicationContext)

        val dueTime = getNextResetTime(Calendar.getInstance())
        val targetResetWorkRequest = OneTimeWorkRequestBuilder<TargetResetWorker>()
            .setInitialDelay(dueTime, TimeUnit.MILLISECONDS)
            .addTag(TargetResetWorker::class.java.name)
            .build()

        workManager.enqueueUniqueWork(TargetResetWorker::class.java.name, ExistingWorkPolicy.REPLACE, targetResetWorkRequest)
    }

    private fun createNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name_target_finish)
            val descriptionText = getString(R.string.channel_description_target_finish)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_TARGET_FINISHED, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}