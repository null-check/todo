package com.arjun.todo.time.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.arjun.todo.data.TargetDao
import com.arjun.todo.util.getNextResetTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "TargetResetWorker"

class TargetResetWorker(
    appContext: Context,
    params: WorkerParameters,
    private val targetDao: TargetDao
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        try {
            if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) { // Todo make customizable day of week
                targetDao.resetAllTargets()
            } else {
                targetDao.resetDailyTargets()
            }

            // Not using PeriodicWorkRequest since its not very accurate https://medium.com/androiddevelopers/workmanager-periodicity-ff35185ff006
            val dueTime = getNextResetTime()
            val dailyWorkRequest = OneTimeWorkRequestBuilder<TargetResetWorker>()
                .setInitialDelay(dueTime, TimeUnit.MILLISECONDS)
                .addTag(TargetResetWorker::class.java.name)
                .build()

            WorkManager.getInstance(applicationContext).enqueue(dailyWorkRequest)
//            Log.d(TAG, "doWork(): WORK DONE!!!")
//            Log.d(TAG, "Set up work " + convertMillisToMins(dueTime) + " mins from now!!!")

            return@withContext Result.success()
        } catch (error: Throwable) {
            Log.e(TAG, "Error resetting target progress: ", error)
            return@withContext Result.failure()
        }
    }
}