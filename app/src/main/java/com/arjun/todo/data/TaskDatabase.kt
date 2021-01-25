package com.arjun.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arjun.todo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val databaseProvider: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = databaseProvider.get().taskDao();

            applicationScope.launch {
                dao.insert(Task("EWosh the dishies"))
                dao.insert(Task("AWosh the dishies2", completed = true))
                dao.insert(Task("DWosh the dishies3"))
                dao.insert(Task("CWosh the dishies4"))
                dao.insert(Task("BWosh the dishies5"))
            }
        }
    }
}