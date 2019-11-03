package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task

interface TasksDataSource {
    suspend fun getTasks(): Result<List<Task>>
    suspend fun getTask(taskId: String): Result<Task>
    suspend fun saveTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun deleteAllTasks()
    suspend fun completeTask(task: Task)
    suspend fun activateTask(task: Task)
    suspend fun clearCompletedTasks()
}