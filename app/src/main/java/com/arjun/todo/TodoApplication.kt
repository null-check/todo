package com.arjun.todo

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arjun.todo.util.convertMillisToMins
import com.arjun.todo.util.getNextResetTime
import com.arjun.todo.workers.TargetResetWorker
import dagger.hilt.android.HiltAndroidApp
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
    }

    private fun setupWorkManager() {
        val workManager = WorkManager.getInstance(applicationContext)

        val dueTime = getNextResetTime()
        val targetResetWorkRequest = OneTimeWorkRequestBuilder<TargetResetWorker>()
            .setInitialDelay(dueTime, TimeUnit.MILLISECONDS)
            .addTag(TargetResetWorker::class.java.name)
            .build()

        workManager.enqueueUniqueWork(TargetResetWorker::class.java.name, ExistingWorkPolicy.REPLACE, targetResetWorkRequest)
//        Log.d(TAG, "Set up work at " + convertMillisToMins(dueTime) + " mins from now!!!")
    }
}