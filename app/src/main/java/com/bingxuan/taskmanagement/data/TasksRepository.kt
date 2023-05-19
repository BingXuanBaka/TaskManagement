package com.bingxuan.taskmanagement.data

import kotlinx.coroutines.flow.Flow

class TasksRepository(private val taskDao: TaskDao) {
    /**
     * Get saved tasks from database
     */
    fun getTasksOrderByID(): Flow<List<Task>> =
        taskDao.getTasksOrderById()

    /**
     * Insert a task to database
     */
    suspend fun insertTask(task: Task) = taskDao.insert(task)
}