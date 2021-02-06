package com.arjun.todo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetDao {

    fun getTargets(searchQuery: String, sortOrder: SortOrder): Flow<List<Target>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getTargetsSortedByDate(searchQuery)
            SortOrder.BY_NAME -> getTargetsSortedByName(searchQuery)
        }

    @Query("SELECT * FROM target_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name")
    fun getTargetsSortedByName(searchQuery: String): Flow<List<Target>>

    @Query("SELECT * FROM target_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY created")
    fun getTargetsSortedByDate(searchQuery: String): Flow<List<Target>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(target: Target)

    @Update
    suspend fun update(target: Target)

    @Delete
    suspend fun delete(target: Target)

    @Query("SELECT * FROM target_table WHERE id = :targetId")
    fun getTarget(targetId: Int): Flow<Target>

    @Query("UPDATE target_table SET progress = 0, beginTimestamp = -1 WHERE period = 'DAILY'")
    fun resetDailyTargets()

    @Query("UPDATE target_table SET progress = 0, beginTimestamp = -1")
    fun resetAllTargets()

    @Query("SELECT * FROM target_table WHERE beginTimestamp != -1")
    fun getActiveTargets(): List<Target>
}