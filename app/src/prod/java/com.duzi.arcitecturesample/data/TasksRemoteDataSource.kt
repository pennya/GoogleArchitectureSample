package com.duzi.arcitecturesample.data

import com.duzi.arcitecturesample.data.source.TasksDataSource

object TasksRemoteDataSource: TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 2000L

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    override suspend fun getTasks(): Result<List<Task>> {
        // TODO   add delay or get data from real server
        return Result.Success(TASKS_SERVICE_DATA.values.toList())
    }

    override suspend fun saveTask(task: Task) {
        TASKS_SERVICE_DATA[task.id] = task
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

}