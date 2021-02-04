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
    suspend fun insert(task: Target)

    @Update
    suspend fun update(task: Target)

    @Delete
    suspend fun delete(task: Target)

    @Query("SELECT * FROM target_table WHERE id = :targetId")
    fun getTarget(targetId: Int): Flow<Target>
}