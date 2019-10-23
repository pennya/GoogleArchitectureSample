package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.coroutines.CoroutineContext

class DefaultTasksRepository(
    private val tasksRemoteRepository: TasksDataSource,
    private val tasksLocalRepository: TasksDataSource,
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

    override suspend fun saveTask(task: Task) {
        cacheAndPerform(task) {
            coroutineScope {
                launch { tasksRemoteRepository.saveTask(it) }
                launch { tasksLocalRepository.saveTask(it) }
            }
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { tasksRemoteRepository.deleteAllTasks() }
                launch { tasksLocalRepository.deleteAllTasks() }
            }
        }
        cachedTasks?.clear()
    }

    override suspend fun completeTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            coroutineScope {
                launch { tasksRemoteRepository.completeTask(task) }
                launch { tasksLocalRepository.completeTask(task) }
            }
        }
    }

    override suspend fun activateTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            coroutineScope {
                launch { tasksRemoteRepository.activateTask(task) }
                launch { tasksLocalRepository.activateTask(task) }
            }
        }
    }

    private suspend fun fetchTasksFromRemoteOrLocal(forceUpdate: Boolean): Result<List<Task>> {
        // 서버 데이터를 가져온다
        val remoteTasks = tasksRemoteRepository.getTasks()
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
        val localTasks = tasksLocalRepository.getTasks()
        if (localTasks is Result.Success)
            return localTasks

        return Result.Error(Exception("Error fetching from remote and local"))
    }

    private suspend fun refreshLocalDataSource(tasks: List<Task>) {
        tasksLocalRepository.deleteAllTasks()
        for (task in tasks) {
            tasksLocalRepository.saveTask(task)
        }
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
}