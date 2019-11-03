package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task

class FakeDataSource(var tasks: MutableList<Task>? = mutableListOf()): TasksDataSource {

    override suspend fun getTasks(): Result<List<Task>> {
        tasks?.let { return Result.Success(it) }
        return Result.Error(
            Exception("Tasks not found")
        )
    }

    override suspend fun saveTask(task: Task) {
        tasks?.add(task)
    }

    override suspend fun deleteAllTasks() {
        tasks?.clear()
    }

    override suspend fun completeTask(task: Task) {
        tasks?.firstOrNull { it.id == task.id }?.let { it.isCompleted = true }
    }

    override suspend fun activateTask(task: Task) {
        tasks?.firstOrNull { it.id == task.id }?.let { it.isCompleted = false }
    }

    override suspend fun clearCompletedTasks() {
        tasks?.removeIf { it.isCompleted }
    }

}