package com.bingxuan.taskmanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "task"
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    var completed: Boolean = false,
    val date: Long? = null,
)
