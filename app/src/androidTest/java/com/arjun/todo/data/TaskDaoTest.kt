package com.arjun.todo.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.arjun.todo.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TaskDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TodoDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TodoDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTask() = runBlockingTest {
        val task = Task("name", false, 1000, 1)
        taskDao.insert(task)

        val tasks = taskDao.getTasksSortedByName("", false).asLiveData().getOrAwaitValue()

        assertThat(tasks).contains(task)
    }

    @Test
    fun deleteTask() = runBlockingTest {
        val task = Task("name", false, 1000, 1)
        taskDao.insert(task)
        taskDao.delete(task)

        val tasks = taskDao.getTasksSortedByName("", false).asLiveData().getOrAwaitValue()

        assertThat(tasks).doesNotContain(task)
    }

    @Test
    fun updateTask() = runBlockingTest {
        val task = Task("name", false, 1000, 1)
        taskDao.insert(task)
        val updatedTask = task.copy(name = "new name")
        taskDao.update(updatedTask)

        val tasks = taskDao.getTasksSortedByName("", false).asLiveData().getOrAwaitValue()

        assertThat(tasks).doesNotContain(task)
        assertThat(tasks).contains(updatedTask)
    }

    @Test
    fun deleteCompleted() = runBlockingTest {
        val task1 = Task("task 1", true, 1000, 1)
        val task2 = Task("task 2", false, 1001, 2)
        taskDao.insert(task1)
        taskDao.insert(task2)
        taskDao.deleteCompleted()

        val tasks = taskDao.getTasksSortedByName("", false).asLiveData().getOrAwaitValue()

        assertThat(tasks).doesNotContain(task1)
        assertThat(tasks).contains(task2)
    }

    @Test
    fun getTasksSortedByName() = runBlockingTest {
        val task1 = Task("a", false, 2000, 1)
        val task2 = Task("b", false, 3000, 2)
        val task3 = Task("c", false, 1000, 3)
        taskDao.insert(task1)
        taskDao.insert(task2)
        taskDao.insert(task3)
        val tasks = taskDao.getTasksSortedByName("", false).asLiveData().getOrAwaitValue()

        assertThat(tasks[0]).isEqualTo(task1)
        assertThat(tasks[1]).isEqualTo(task2)
        assertThat(tasks[2]).isEqualTo(task3)
    }

    @Test
    fun getTasksSortedByDate() = runBlockingTest {
        val task1 = Task("a", false, 2000, 1)
        val task2 = Task("b", false, 3000, 2)
        val task3 = Task("c", false, 1000, 3)
        taskDao.insert(task1)
        taskDao.insert(task2)
        taskDao.insert(task3)
        val tasks = taskDao.getTasksSortedByDate("", false).asLiveData().getOrAwaitValue()

        assertThat(tasks[0]).isEqualTo(task3)
        assertThat(tasks[1]).isEqualTo(task1)
        assertThat(tasks[2]).isEqualTo(task2)
    }

    @Test
    fun getTasks_containsCompletedAtLast() = runBlockingTest {
        val task1 = Task("a", true, 2000, 1)
        val task2 = Task("b", false, 3000, 2)
        val task3 = Task("c", false, 1000, 3)
        taskDao.insert(task1)
        taskDao.insert(task2)
        taskDao.insert(task3)
        val tasks = taskDao.getTasks("", SortOrder.BY_NAME, false).asLiveData().getOrAwaitValue()

        assertThat(tasks[0]).isEqualTo(task2)
        assertThat(tasks[1]).isEqualTo(task3)
        assertThat(tasks[2]).isEqualTo(task1)
    }

    @Test
    fun getTasks_searchQuery() = runBlockingTest {
        val task1 = Task("a", false, 2000, 1)
        val task2 = Task("b", false, 3000, 2)
        val task3 = Task("c", false, 1000, 3)
        taskDao.insert(task1)
        taskDao.insert(task2)
        taskDao.insert(task3)
        val tasks = taskDao.getTasks("a", SortOrder.BY_NAME, false).asLiveData().getOrAwaitValue()

        assertThat(tasks).contains(task1)
        assertThat(tasks).doesNotContain(task2)
        assertThat(tasks).doesNotContain(task3)
    }

    @Test
    fun getTasks_hideCompleted() = runBlockingTest {
        val task1 = Task("a", false, 2000, 1)
        val task2 = Task("b", true, 3000, 2)
        val task3 = Task("c", false, 1000, 3)
        taskDao.insert(task1)
        taskDao.insert(task2)
        taskDao.insert(task3)
        val tasks = taskDao.getTasks("", SortOrder.BY_NAME, true).asLiveData().getOrAwaitValue()

        assertThat(tasks).contains(task1)
        assertThat(tasks).doesNotContain(task2)
        assertThat(tasks).contains(task3)
    }
}