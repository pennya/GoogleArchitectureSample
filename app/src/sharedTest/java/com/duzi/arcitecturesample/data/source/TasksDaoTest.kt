package com.duzi.arcitecturesample.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duzi.arcitecturesample.MainCoroutineRule
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.local.TaskDatabase
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
class TasksDaoTest {
    private lateinit var taskDatabase: TaskDatabase

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        taskDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDatabase::class.java
        ).build()
    }

    @After
    fun finish() = taskDatabase.close()

    @Test
    fun insertTaskAndGetTasks() = runBlockingTest {
        val task = Task("Title1", "Description1")
        taskDatabase.taskDao().insertTask(task)

        val tasks = taskDatabase.taskDao().getTasks()

        assertThat(tasks.size, `is`(1))
        assertThat(tasks[0].id, `is`(task.id))
        assertThat(tasks[0].title, `is`(task.title))
        assertThat(tasks[0].description, `is`(task.description))
        assertThat(tasks[0].isCompleted, `is`(task.isCompleted))
    }
}