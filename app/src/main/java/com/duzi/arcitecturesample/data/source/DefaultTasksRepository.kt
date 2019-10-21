package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class DefaultTasksRepository(
    private val tasksRemoteRepository: TasksDataSource,
    private val tasksLocalRepository: TasksDataSource
): TasksRepository {

    private var cachedTasks: ConcurrentMap<String, Task>? = null

    override fun getTasks(forceUpdate: Boolean): Result<List<Task>> {

        // 강제 업데이트가 아니면 캐시에서
        if (!forceUpdate) {
            cachedTasks?.let { cachedTasks ->
                return Result.Success(cachedTasks.values.sortedBy { it.id })
            }
        }

        val newTasks = fetchTasksFromRemoteOrLocal(forceUpdate)
        // 데이터 가져와서 캐시
        (newTasks as? Result.Success)?.let { refreshCache(it.data) }

        cachedTasks?.values?.let { tasks ->
            return Result.Success(tasks.sortedBy { it.id })
        }

        (newTasks as? Result.Success)?.let {
            if (it.data.isEmpty()) {
                return Result.Success(it.data)
            }
        }

        return Result.Error(Exception("Illegal state"))
    }

    override fun saveTask(task: Task) {
        tasksRemoteRepository.saveTask(task)
        tasksLocalRepository.saveTask(task)
    }

    override fun deleteAllTasks() {
        tasksRemoteRepository.deleteAllTasks()
        tasksLocalRepository.deleteAllTasks()
        cachedTasks?.clear()
    }

    override fun completeTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            tasksRemoteRepository.completeTask(it)
            tasksLocalRepository.completeTask(it)
        }
    }

    override fun activateTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            tasksRemoteRepository.activateTask(it)
            tasksLocalRepository.activateTask(it)
        }
    }

    private fun fetchTasksFromRemoteOrLocal(forceUpdate: Boolean): Result<List<Task>> {
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

    private fun refreshLocalDataSource(tasks: List<Task>) {
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

    private fun cacheAndPerform(task: Task, perform: (Task) -> Unit) {
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