package com.duzi.arcitecturesample

import android.app.Application
import com.duzi.arcitecturesample.data.source.TasksRepository

class TodoApplication: Application() {

    val taskRepository: TasksRepository
        get() = ServiceLocator.provideTasksRepository(this)

    override fun onCreate() {
        super.onCreate()
    }
}