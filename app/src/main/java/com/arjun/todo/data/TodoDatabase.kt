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

            // Mock data for testing
            applicationScope.launch {
                taskDao.insert(Task("Wash the dishes"))
                taskDao.insert(Task("Buy groceries", completed = true))
                taskDao.insert(Task("Cook lunch"))
                taskDao.insert(Task("Finish a book"))
                taskDao.insert(Task("Call Elon Musk"))

                targetDao.insert(Target("Work", 8*60*60, 0))
                targetDao.insert(Target("Learn", 4*60*60, 1*60*60))
                targetDao.insert(Target("Code", 30*60, 10*60, beginTimestamp = System.currentTimeMillis()))
                targetDao.insert(Target("Exercise", 2*60*60, 10*60))
            }
        }
    }
}