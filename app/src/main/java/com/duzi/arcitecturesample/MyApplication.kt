package com.duzi.arcitecturesample

import android.app.Application
import com.duzi.arcitecturesample.data.TasksRepository

class MyApplication: Application() {

    val repository: TasksRepository
        get() = object:TasksRepository {

        }

    override fun onCreate() {
        super.onCreate()
    }
}