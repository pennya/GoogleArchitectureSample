package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task

object TasksLocalDataSource: TasksDataSource {
    override fun getTasks(): Result<List<Task>> {
        return Result.Success(emptyList())
    }

    override fun saveTask(task: Task) {
        // insert
    }

    override fun deleteAllTasks() {
        // delete all
    }

    override fun completeTask(task: Task) {
        // updateCompleted true
    }

    override fun activateTask(task: Task) {
        // updateCompleted false
    }

}