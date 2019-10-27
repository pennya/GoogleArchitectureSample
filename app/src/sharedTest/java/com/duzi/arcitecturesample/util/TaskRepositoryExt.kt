package com.duzi.arcitecturesample.util

import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.TasksRepository
import kotlinx.coroutines.runBlocking

fun TasksRepository.saveTaskBlocking(task: Task) = runBlocking {
    this@saveTaskBlocking.saveTask(task)
}

fun TasksRepository.getTaskBlocking(forceUpdate: Boolean) = runBlocking {
    this@getTaskBlocking.getTasks(forceUpdate)
}

fun TasksRepository.deleteAllTasksBlocking() = runBlocking {
    this@deleteAllTasksBlocking.deleteAllTasks()
}