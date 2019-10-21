package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task

interface TasksRepository {
    fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>
    fun saveTask(task: Task)
    fun deleteAllTasks()
    fun completeTask(task: Task)
    fun activateTask(task: Task)
}