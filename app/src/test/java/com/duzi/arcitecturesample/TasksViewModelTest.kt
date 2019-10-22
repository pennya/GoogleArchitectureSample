package com.duzi.arcitecturesample

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.duzi.arcitecturesample.data.FakeTasksRemoteDataSource
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.TasksFilterType
import com.duzi.arcitecturesample.data.source.DefaultTasksRepository
import com.duzi.arcitecturesample.data.source.TasksLocalDataSource
import com.duzi.arcitecturesample.util.LiveDataTestUtil
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {

    private lateinit var tasksViewModel: MainViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        val repository = DefaultTasksRepository(FakeTasksRemoteDataSource, TasksLocalDataSource)

        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        repository.saveTask(task1)
        repository.saveTask(task2)
        repository.saveTask(task3)

        tasksViewModel = MainViewModel(repository)
    }

    @After
    fun releaseViewModel() {
        FakeTasksRemoteDataSource.deleteAllTasks()
        TasksLocalDataSource.deleteAllTasks()
    }

    @Test
    fun loadAllTasksFromRepository_loadingTogglesAndDataLoaded() {
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        tasksViewModel.loadTasks(true)

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading)).isFalse()

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.items)).hasSize(3)
    }

    @Test
    fun loadActiveTasksFromRepositoryAndLoadIntoView() {
        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)

        tasksViewModel.loadTasks(true)

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading)).isFalse()

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.items)).hasSize(1)
    }

    @Test
    fun loadCompletedTasksFromRepositoryAndLoadIntoView() {
        tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS)

        tasksViewModel.loadTasks(true)

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading)).isFalse()

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.items)).hasSize(2)
    }
}