package com.duzi.arcitecturesample

import android.content.Context
import androidx.room.Room
import com.duzi.arcitecturesample.data.TasksRemoteDataSource
import com.duzi.arcitecturesample.data.source.DefaultTasksRepository
import com.duzi.arcitecturesample.data.source.TasksDataSource
import com.duzi.arcitecturesample.data.source.TasksRepository
import com.duzi.arcitecturesample.data.source.local.TaskDatabase
import com.duzi.arcitecturesample.data.source.local.TasksLocalDataSource

object ServiceLocator {
    private var tasksRepository: TasksRepository? = null
    private var database: TaskDatabase? = null

    fun provideTasksRepository(context: Context): TasksRepository {
        return tasksRepository ?: createTasksRepository(context)
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        return DefaultTasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context))
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = this.database ?: createDatabase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDatabase(context: Context): TaskDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            TaskDatabase::class.java,
            "Tasks.db"
        ).build()
        database = result
        return result
    }
}