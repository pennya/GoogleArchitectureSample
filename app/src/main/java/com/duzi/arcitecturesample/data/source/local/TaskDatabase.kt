package com.duzi.arcitecturesample.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duzi.arcitecturesample.data.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TasksDao
}