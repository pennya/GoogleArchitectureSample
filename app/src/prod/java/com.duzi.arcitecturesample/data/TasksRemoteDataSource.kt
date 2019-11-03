package com.duzi.arcitecturesample.data

import com.duzi.arcitecturesample.data.source.TasksDataSource
import kotlinx.coroutines.delay

object TasksRemoteDataSource: TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    override suspend fun getTasks(): Result<List<Task>> {
        val tasks = TASKS_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLIS)
        return Result.Success(tasks)
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        delay(SERVICE_LATENCY_IN_MILLIS)
        TASKS_SERVICE_DATA[taskId]?.let {
            return Result.Success(it)
        }
        return Result.Error(
            Exception("Task not found!")
        )
    }

    override suspend fun saveTask(task: Task) {
        TASKS_SERVICE_DATA[task.id] = task
    }

    override suspend fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }

    override suspend fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.id)
        TASKS_SERVICE_DATA[task.id] = completedTask
    }

    override suspend fun activateTask(task: Task) {
        val activatedTask = Task(task.title, task.description, false, task.id)
        TASKS_SERVICE_DATA[task.id] = activatedTask
    }

    override suspend fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

}