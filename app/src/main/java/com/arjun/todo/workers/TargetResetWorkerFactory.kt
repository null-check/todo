package com.arjun.todo.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.arjun.todo.data.TargetDao

class TargetResetWorkerFactory(
    private val targetDao: TargetDao
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            TargetResetWorker::class.java.name ->
                TargetResetWorker(appContext, workerParameters, targetDao)
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }
    }

}