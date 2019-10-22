package com.duzi.arcitecturesample.data.source.local

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.TasksDataSource

class TasksLocalDataSource internal constructor(
    private val tasksDao: TasksDao
): TasksDataSource {
    override fun getTasks(): Result<List<Task>> {
        return Result.Success(tasksDao.getTasks())
    }

    override fun saveTask(task: Task) {
        tasksDao.insertTask(task)
    }

    override fun deleteAllTasks() {
        tasksDao.deleteTasks()
    }

    override fun completeTask(task: Task) {
        tasksDao.updateCompleted(task.id, true)
    }

    override fun activateTask(task: Task) {
        tasksDao.updateCompleted(task.id, false)
    }

}