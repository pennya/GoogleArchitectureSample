package com.duzi.arcitecturesample.data

import com.duzi.arcitecturesample.data.source.TasksDataSource
import java.util.LinkedHashMap

object FakeTasksRemoteDataSource: TasksDataSource {

    private var TASKS_SERVICE_DATA: LinkedHashMap<String, Task> = LinkedHashMap()

    override fun getTasks(): Result<List<Task>> {
        return Result.Success(TASKS_SERVICE_DATA.values.toList())
    }

    override fun saveTask(task: Task) {
        TASKS_SERVICE_DATA[task.id] = task
    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.id)
        TASKS_SERVICE_DATA[task.id] = completedTask
    }

    override fun activateTask(task: Task) {
        val activatedTask = Task(task.title, task.description, false, task.id)
        TASKS_SERVICE_DATA[task.id] = activatedTask
    }

}