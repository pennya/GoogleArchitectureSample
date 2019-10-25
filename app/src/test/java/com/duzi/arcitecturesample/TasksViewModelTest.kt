package com.duzi.arcitecturesample

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.TasksFilterType
import com.duzi.arcitecturesample.util.LiveDataTestUtil
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TasksViewModelTest {

    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var tasksRepository: FakeRepositoryForTest

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        tasksRepository = FakeRepositoryForTest()

        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)
        
        tasksViewModel = TasksViewModel(tasksRepository)
    }

    @After
    fun releaseViewModel() = mainCoroutineRule.runBlockingTest {
        tasksViewModel.deleteAllTasks()
    }

    @Test
    fun loadAllTasksFromRepository_loadingTogglesAndDataLoaded() {
        mainCoroutineRule.pauseDispatcher()

        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        tasksViewModel.loadTasks(true)

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading)).isTrue()

        mainCoroutineRule.resumeDispatcher()

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading)).isFalse()

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.items)).hasSize(3)
    }

    @Test
    fun loadActiveTasksFromRepositoryAndLoadIntoView() {
        mainCoroutineRule.pauseDispatcher()

        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)

        tasksViewModel.loadTasks(true)

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading)).isTrue()

        mainCoroutineRule.resumeDispatcher()

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading)).isFalse()

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.items)).hasSize(1)
    }

    @Test
    fun loadCompletedTasksFromRepositoryAndLoadIntoView() {
        mainCoroutineRule.pauseDispatcher()

        tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS)

        tasksViewModel.loadTasks(true)

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading)).isTrue()

        mainCoroutineRule.resumeDispatcher()

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading)).isFalse()

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.items)).hasSize(2)
    }
}