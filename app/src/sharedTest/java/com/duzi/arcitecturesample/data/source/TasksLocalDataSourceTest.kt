package com.duzi.arcitecturesample.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duzi.arcitecturesample.MainCoroutineRule
import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.local.TaskDatabase
import com.duzi.arcitecturesample.data.source.local.TasksLocalDataSource
import com.duzi.arcitecturesample.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TasksLocalDataSourceTest {

    private lateinit var database: TaskDatabase
    private lateinit var localDataSource: TasksLocalDataSource

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
    }

    @After
    fun finish() = database.close()

    @Test
    fun completeTask_retrievedTaskIsComplete() = runBlockingTest {
        val newTask = Task("title")
        localDataSource.saveTask(newTask)

        localDataSource.completeTask(newTask)

        val result = localDataSource.getTasks()
        assertThat(result.succeeded, `is`(true))

        result as Result.Success
        assertThat(result.data[0].title, `is`("title"))
        assertThat(result.data[0].isCompleted, `is`(true))
    }

}