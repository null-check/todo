package com.arjun.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arjun.todo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class, Target::class], version = 2, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun targetDao(): TargetDao

    class Callback @Inject constructor(
        private val databaseProvider: Provider<TodoDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val taskDao = databaseProvider.get().taskDao();
            val targetDao = databaseProvider.get().targetDao();

            applicationScope.launch {
                taskDao.insert(Task("EWosh the dishies"))
                taskDao.insert(Task("AWosh the dishies2", completed = true))
                taskDao.insert(Task("DWosh the dishies3"))
                taskDao.insert(Task("CWosh the dishies4"))
                taskDao.insert(Task("BWosh the dishies5"))

                targetDao.insert(Target("Work", 8, 0))
                targetDao.insert(Target("Learn", 4, 1))
            }
        }
    }
}