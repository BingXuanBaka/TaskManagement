package com.bingxuan.taskmanagement.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM task ORDER BY id")
    fun getTasksOrderById(): Flow<List<Task>>

    @Query("SELECT * FROM task ORDER BY date")
    fun getTasksOrderByDate(): Flow<List<Task>>

    @Query("SELECT * FROM task ORDER BY name ASC")
    fun getTasksOrderByName(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id LIMIT 1")
    fun getTaskByID(id: Int): Flow<Task?>
}
