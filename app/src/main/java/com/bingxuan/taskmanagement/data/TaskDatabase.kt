package com.bingxuan.taskmanagement.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Task::class],
    version = 1
)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun getDao(): TaskDao

    companion object{
        @Volatile
        private var Instance: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    context = context,
                    klass = TaskDatabase::class.java,
                    name = "tasks"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

}