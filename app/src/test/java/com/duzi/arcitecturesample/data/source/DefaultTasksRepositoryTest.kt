package com.duzi.arcitecturesample.data.source

import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class DefaultTasksRepositoryTest {
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val newTask = Task("Title new", "Description new")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }
    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource
    private lateinit var tasksRepository: DefaultTasksRepository

    @Before
    fun createRepository() {
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
        tasksRepository = DefaultTasksRepository(tasksRemoteDataSource, tasksLocalDataSource,
            Dispatchers.Unconfined)
    }

    @ExperimentalCoroutinesApi
    @After
    fun finish() = runBlockingTest {
        tasksRepository.deleteAllTasks()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getTasks_emptyRepositoryAndUninitializedCache() = runBlockingTest {
        val emptySource = FakeDataSource()
        val tasksRepository = DefaultTasksRepository(
            emptySource, emptySource, Dispatchers.Unconfined
        )

        assertThat(tasksRepository.getTasks() is Result.Success).isTrue()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getTasks_repositoryCachesAfterFirstApiCall() = runBlockingTest {
        val initial = tasksRepository.getTasks()

        tasksRemoteDataSource.tasks = newTasks.toMutableList()

        val second = tasksRepository.getTasks()

        assertThat(second).isEqualTo(initial)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
        val tasks = tasksRepository.getTasks() as Result.Success

        assertThat(tasks.data).isEqualTo(remoteTasks)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveTask_savesToCacheLocalAndRemote() = runBlockingTest {
        assertThat(tasksRemoteDataSource.tasks).doesNotContain(newTask)
        assertThat(tasksLocalDataSource.tasks).doesNotContain(newTask)
        assertThat((tasksRepository.getTasks() as? Result.Success)?.data).doesNotContain(newTask)

        tasksRepository.saveTask(newTask)

        assertThat(tasksRemoteDataSource.tasks).contains(newTask)
        assertThat(tasksLocalDataSource.tasks).contains(newTask)

        val result = tasksRepository.getTasks() as? Result.Success
        assertThat(result?.data).contains(newTask)
    }
}