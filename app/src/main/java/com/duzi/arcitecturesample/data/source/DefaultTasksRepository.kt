package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.util.EspressoIdlingResource
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class DefaultTasksRepository(
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TasksRepository {

    private var cachedTasks: ConcurrentMap<String, Task>? = null

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {

        return withContext(ioDispatcher) {
            // 강제 업데이트가 아니면 캐시에서
            if (!forceUpdate) {
                cachedTasks?.let { cachedTasks ->
                    return@withContext Result.Success(cachedTasks.values.sortedBy { it.id })
                }
            }
            val newTasks = fetchTasksFromRemoteOrLocal(forceUpdate)
            // 데이터 가져와서 캐시
            (newTasks as? Result.Success)?.let { refreshCache(it.data) }

            cachedTasks?.values?.let { tasks ->
                return@withContext Result.Success(tasks.sortedBy { it.id })
            }

            (newTasks as? Result.Success)?.let {
                if (it.data.isEmpty()) {
                    return@withContext Result.Success(it.data)
                }
            }

            return@withContext Result.Error(Exception("Illegal state"))
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        return withContext(ioDispatcher) {
            // Respond immediately with cache if available
            if (!forceUpdate) {
                getTaskWithId(taskId)?.let {
                    EspressoIdlingResource.decrement() // Set app as idle.
                    return@withContext Result.Success(it)
                }
            }

            val newTask = fetchTaskFromRemoteOrLocal(taskId, forceUpdate)

            // Refresh the cache with the new tasks
            (newTask as? Result.Success)?.let { cacheTask(it.data) }

            return@withContext newTask
        }
    }

    override suspend fun saveTask(task: Task) {
        cacheAndPerform(task) {
            coroutineScope {
                launch { tasksRemoteDataSource.saveTask(it) }
                launch { tasksLocalDataSource.saveTask(it) }
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        coroutineScope {
            launch { tasksRemoteDataSource.deleteTask(taskId) }
            launch { tasksLocalDataSource.deleteTask(taskId) }
        }

        cachedTasks?.remove(taskId)
    }

    override suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteAllTasks() }
                launch { tasksLocalDataSource.deleteAllTasks() }
            }
        }
        cachedTasks?.clear()
    }

    override suspend fun completeTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            coroutineScope {
                launch { tasksRemoteDataSource.completeTask(task) }
                launch { tasksLocalDataSource.completeTask(task) }
            }
        }
    }

    override suspend fun activateTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            coroutineScope {
                launch { tasksRemoteDataSource.activateTask(task) }
                launch { tasksLocalDataSource.activateTask(task) }
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { tasksRemoteDataSource.clearCompletedTasks() }
            launch { tasksLocalDataSource.clearCompletedTasks() }
        }
        withContext(ioDispatcher) {
            cachedTasks?.entries?.removeAll { it.value.isCompleted }
        }
    }

    private suspend fun fetchTasksFromRemoteOrLocal(forceUpdate: Boolean): Result<List<Task>> {
        // 서버 데이터를 가져온다
        val remoteTasks = tasksRemoteDataSource.getTasks()
        when (remoteTasks) {
            is Result.Error -> "Remote data source fetch failed"
            is Result.Success -> {
                // 로컬 값을 전부 지우고 새 값으로 교체
                refreshLocalDataSource(remoteTasks.data)
                return remoteTasks
            }
            else -> throw IllegalStateException()
        }

        if (forceUpdate) {
            return Result.Error(Exception("Can't force refresh"))
        }

        // 로컬 데이터를 가져온다
        val localTasks = tasksLocalDataSource.getTasks()
        if (localTasks is Result.Success)
            return localTasks

        return Result.Error(Exception("Error fetching from remote and local"))
    }

    private suspend fun fetchTaskFromRemoteOrLocal(taskId: String, forceUpdate: Boolean)
            : Result<Task> {

        val remoteTask = tasksRemoteDataSource.getTask(taskId)
        when (remoteTask) {
            is Result.Error -> "Remote data source fetch failed"
            is Result.Success -> {
                refreshLocalDataSource(remoteTask.data)
                return remoteTask
            }
            else -> throw IllegalStateException()
        }

        if (forceUpdate) {
            return Result.Error(Exception("Refresh failed"))
        }

        val localTasks = tasksLocalDataSource.getTask(taskId)
        if (localTasks is Result.Success) return localTasks
        return Result.Error(Exception("Error fetching from remote and local"))
    }

    private suspend fun refreshLocalDataSource(tasks: List<Task>) {
        tasksLocalDataSource.deleteAllTasks()
        for (task in tasks) {
            tasksLocalDataSource.saveTask(task)
        }
    }

    private suspend fun refreshLocalDataSource(task: Task) {
        tasksLocalDataSource.saveTask(task)
    }

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks?.clear()
        tasks.sortedBy { it.id }.forEach {
            cacheAndPerform(it) {}
        }
    }

    private inline fun cacheAndPerform(task: Task, perform: (Task) -> Unit) {
        val cachedTask = cacheTask(task)
        perform(cachedTask)
    }

    private fun cacheTask(task: Task): Task {
        val cachedTask = Task(task.title, task.description, task.isCompleted, task.id)
        if (cachedTasks == null) {
            cachedTasks = ConcurrentHashMap()
        }
        cachedTasks?.put(cachedTask.id, cachedTask)
        return cachedTask
    }

    private fun getTaskWithId(id: String) = cachedTasks?.get(id)
}