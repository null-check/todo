package com.arjun.todo.time.workers

import androidx.work.DelegatingWorkerFactory
import com.arjun.todo.data.TargetDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IoSchedWorkerFactory @Inject constructor(
    targetDao: TargetDao
) : DelegatingWorkerFactory() {
    init {
        addFactory(TargetResetWorkerFactory(targetDao))
    }
}