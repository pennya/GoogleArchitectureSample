package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task

interface TasksDataSource {
    fun getTasks(): Result<List<Task>>
    fun saveTask(task: Task)
    fun deleteAllTasks()
    fun completeTask(task: Task)
    fun activateTask(task: Task)
}