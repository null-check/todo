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
class TargetDaoTest {

    @get:Rule
    val instantTargetExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TodoDatabase
    private lateinit var targetDao: TargetDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TodoDatabase::class.java
        ).allowMainThreadQueries().build()
        targetDao = database.targetDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTarget() = runBlockingTest {
        val target = Target("name", 5, 1, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        targetDao.insert(target)

        val targets = targetDao.getTargetsSortedByName("").asLiveData().getOrAwaitValue()

        assertThat(targets).contains(target)
    }

    @Test
    fun deleteTarget() = runBlockingTest {
        val target = Target("name", 5, 1, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        targetDao.insert(target)
        targetDao.delete(target)

        val targets = targetDao.getTargetsSortedByName("").asLiveData().getOrAwaitValue()

        assertThat(targets).doesNotContain(target)
    }

    @Test
    fun updateTarget() = runBlockingTest {
        val target = Target("name", 5, 1, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        targetDao.insert(target)
        val updatedTarget = target.copy(name = "new name", targetAmount = 4, progress = 3, period = TargetPeriod.DAILY.name, beginTimestamp = 2000)
        targetDao.update(updatedTarget)

        val targets = targetDao.getTargetsSortedByName("").asLiveData().getOrAwaitValue()

        assertThat(targets).doesNotContain(target)
        assertThat(targets).contains(updatedTarget)
    }

    @Test
    fun getTargetsSortedByName() = runBlockingTest {
        val target1 = Target("c", 5, 1, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        val target2 = Target("b", 5, 1, TargetPeriod.WEEKLY.name, -1, 3000, 2)
        val target3 = Target("a", 5, 1, TargetPeriod.WEEKLY.name, -1, 2000, 3)
        targetDao.insert(target1)
        targetDao.insert(target2)
        targetDao.insert(target3)
        val targets = targetDao.getTargetsSortedByName("").asLiveData().getOrAwaitValue()

        assertThat(targets[0]).isEqualTo(target3)
        assertThat(targets[1]).isEqualTo(target2)
        assertThat(targets[2]).isEqualTo(target1)
    }

    @Test
    fun getTargetsSortedByDate() = runBlockingTest {
        val target1 = Target("c", 5, 1, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        val target2 = Target("b", 5, 1, TargetPeriod.WEEKLY.name, -1, 3000, 2)
        val target3 = Target("a", 5, 1, TargetPeriod.WEEKLY.name, -1, 2000, 3)
        targetDao.insert(target1)
        targetDao.insert(target2)
        targetDao.insert(target3)
        val targets = targetDao.getTargetsSortedByDate("").asLiveData().getOrAwaitValue()

        assertThat(targets[0]).isEqualTo(target1)
        assertThat(targets[1]).isEqualTo(target3)
        assertThat(targets[2]).isEqualTo(target2)
    }

    @Test
    fun getTargets_searchQuery() = runBlockingTest {
        val target1 = Target("c", 5, 1, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        val target2 = Target("b", 5, 1, TargetPeriod.WEEKLY.name, -1, 3000, 2)
        val target3 = Target("a", 5, 1, TargetPeriod.WEEKLY.name, -1, 2000, 3)
        targetDao.insert(target1)
        targetDao.insert(target2)
        targetDao.insert(target3)
        val targets = targetDao.getTargets("a", SortOrder.BY_NAME).asLiveData().getOrAwaitValue()

        assertThat(targets).contains(target3)
        assertThat(targets).doesNotContain(target1)
        assertThat(targets).doesNotContain(target2)
    }

    @Test
    fun getTarget() = runBlockingTest {
        val target1 = Target("c", 5, 1, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        val target2 = Target("b", 5, 1, TargetPeriod.WEEKLY.name, -1, 3000, 2)
        val target3 = Target("a", 5, 1, TargetPeriod.WEEKLY.name, -1, 2000, 3)
        targetDao.insert(target1)
        targetDao.insert(target2)
        targetDao.insert(target3)
        val target = targetDao.getTarget(target1.id).asLiveData().getOrAwaitValue()

        assertThat(target).isEqualTo(target1)
    }

    @Test
    fun resetAllTargets() = runBlockingTest {
        val target1 = Target("a", 5, 0, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        val target2= Target("b", 10, 0, TargetPeriod.DAILY.name, -1, 2000, 2)
        targetDao.insert(target1)
        targetDao.insert(target2)
        val updatedTarget1 = target1.copy(progress = 3, beginTimestamp = 1500)
        val updatedTarget2 = target2.copy(progress = 6, beginTimestamp = 2500)
        targetDao.update(updatedTarget1)
        targetDao.update(updatedTarget2)
        targetDao.resetAllTargets()

        val targets = targetDao.getTargets("", SortOrder.BY_NAME).asLiveData().getOrAwaitValue()

        assertThat(targets).contains(target1)
        assertThat(targets).contains(target2)
    }

    @Test
    fun resetDailyTargets() = runBlockingTest {
        val target1 = Target("a", 5, 0, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        val target2= Target("b", 10, 0, TargetPeriod.DAILY.name, -1, 2000, 2)
        targetDao.insert(target1)
        targetDao.insert(target2)
        val updatedTarget1 = target1.copy(progress = 3, beginTimestamp = 1500)
        val updatedTarget2 = target2.copy(progress = 6, beginTimestamp = 2500)
        targetDao.update(updatedTarget1)
        targetDao.update(updatedTarget2)
        targetDao.resetDailyTargets()

        val targets = targetDao.getTargets("", SortOrder.BY_NAME).asLiveData().getOrAwaitValue()

        assertThat(targets).contains(updatedTarget1)
        assertThat(targets).contains(target2)
    }

    @Test
    fun getActiveTargets() = runBlockingTest {
        val target1 = Target("c", 5, 1, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        val target2 = Target("b", 5, 1, TargetPeriod.WEEKLY.name, 3500, 3000, 2)
        targetDao.insert(target1)
        targetDao.insert(target2)
        val targets = targetDao.getActiveTargets()

        assertThat(targets).doesNotContain(target1)
        assertThat(targets).contains(target2)
    }

    @Test
    fun observeTotalTargetAmount() = runBlockingTest {
        val target1 = Target("c", 5, 1, TargetPeriod.WEEKLY.name, -1, 1000, 1)
        val target2 = Target("b", 10, 1, TargetPeriod.WEEKLY.name, -1, 3000, 2)
        val target3 = Target("a", 0, 1, TargetPeriod.WEEKLY.name, -1, 2000, 3)
        targetDao.insert(target1)
        targetDao.insert(target2)
        targetDao.insert(target3)
        val totalTargetAmount = targetDao.observeTotalTargetAmount().getOrAwaitValue()

        assertThat(totalTargetAmount).isEqualTo(15)
    }
}