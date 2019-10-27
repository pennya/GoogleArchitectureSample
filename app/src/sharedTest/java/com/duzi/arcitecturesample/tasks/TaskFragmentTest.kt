package com.duzi.arcitecturesample.tasks

import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duzi.arcitecturesample.FakeRepositoryForTest
import com.duzi.arcitecturesample.R
import com.duzi.arcitecturesample.ServiceLocator
import com.duzi.arcitecturesample.TasksActivity
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.TasksRepository
import com.duzi.arcitecturesample.util.saveTaskBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun setup() {
        repository = FakeRepositoryForTest()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun finish() = runBlockingTest {
        repository.deleteAllTasks()
    }

    @Test
    fun displayTask_whenRepositoryHasData() {
        repository.saveTaskBlocking(Task("Title1", "Description1"))

        launchActivity()

        onView(withText("Title1")).check(matches(isDisplayed()))
    }

    private fun launchActivity(): ActivityScenario<TasksActivity> {
        val activityScenario = launch(TasksActivity::class.java)
        activityScenario.onActivity { activity ->
            (activity.findViewById(R.id.tasks_list) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }
}