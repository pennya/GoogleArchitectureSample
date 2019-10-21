package com.duzi.arcitecturesample

import android.content.Context
import com.duzi.arcitecturesample.data.FakeTasksRemoteDataSource
import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.DefaultTasksRepository
import com.duzi.arcitecturesample.data.source.TasksDataSource
import com.duzi.arcitecturesample.data.source.TasksLocalDataSource
import com.duzi.arcitecturesample.data.source.TasksRepository

object ServiceLocator {
    var tasksRepository: TasksRepository? = null

    fun provideTasksRepository(context: Context): TasksRepository {
        return tasksRepository ?: createTasksRepository(context)
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        return DefaultTasksRepository(FakeTasksRemoteDataSource, createTaskLocalDataSource(context))
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {

        // TODO  Room database
        return TasksLocalDataSource
    }
}