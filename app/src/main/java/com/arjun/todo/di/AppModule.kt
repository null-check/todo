package com.arjun.todo.di

import android.app.Application
import androidx.room.Room
import androidx.work.Configuration
import com.arjun.todo.BuildConfig
import com.arjun.todo.data.TargetDao
import com.arjun.todo.data.TaskDao
import com.arjun.todo.data.TodoDatabase
import com.arjun.todo.workers.IoSchedWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: TodoDatabase.Callback
    ): TodoDatabase = Room.databaseBuilder(app, TodoDatabase::class.java, "todo_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(db: TodoDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideTargetDao(db: TodoDatabase): TargetDao = db.targetDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun provideWorkManagerConfiguration(
        ioSchedWorkerFactory: IoSchedWorkerFactory
    ): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .setWorkerFactory(ioSchedWorkerFactory)
            .build()
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope