package com.duzi.arcitecturesample.data.source.local

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.TasksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksLocalDataSource internal constructor(
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TasksDataSource {

    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(tasksDao.getTasks())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = tasksDao.getTaskById(taskId)
            if (task != null) {
                return@withContext Result.Success(task)
            } else {
                return@withContext Result.Error(Exception("Task not found!"))
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.insertTask(task)
    }

    override suspend fun deleteTask(taskId: String) {
        tasksDao.deleteTaskById(taskId)
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        tasksDao.deleteTasks()
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.id, true)
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.id, false)
    }

    override suspend fun clearCompletedTasks() = withContext<Unit>(ioDispatcher) {
        tasksDao.deleteCompletedTasks()
    }
}