package com.duzi.arcitecturesample

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.TasksFilterType
import com.duzi.arcitecturesample.util.LiveDataTestUtil
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {

    private lateinit var tasksViewModel: MainViewModel
    private lateinit var tasksRepository: FakeRepositoryForTest

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        tasksRepository = FakeRepositoryForTest()

        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)
        
        tasksViewModel = MainViewModel(tasksRepository)
    }

    @After
    fun releaseViewModel() {
        tasksRepository.deleteAllTasks()
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