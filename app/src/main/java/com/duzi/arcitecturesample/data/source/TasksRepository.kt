package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task

interface TasksRepository {
    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>
    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task>
    suspend fun saveTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun deleteAllTasks()
    suspend fun completeTask(task: Task)
    suspend fun activateTask(task: Task)
    suspend fun clearCompletedTasks()
}