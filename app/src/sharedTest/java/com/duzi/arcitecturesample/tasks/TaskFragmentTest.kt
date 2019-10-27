package com.duzi.arcitecturesample.tasks

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duzi.arcitecturesample.FakeRepositoryForTest
import com.duzi.arcitecturesample.R
import com.duzi.arcitecturesample.ServiceLocator
import com.duzi.arcitecturesample.TasksActivity
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.TasksRepository
import com.duzi.arcitecturesample.util.saveTaskBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

@ExperimentalCoroutinesApi
@LooperMode(LooperMode.Mode.PAUSED)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
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

    @Test
    fun displayActiveTask() {
        repository.saveTaskBlocking(Task("Title1", "Description1"))

        launchActivity()

        onView(withText("Title1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText("Active")).perform(click())
        onView(withText("Title1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText("Completed")).perform(click())
        onView(withText("Title1")).check(matches(IsNot.not(isDisplayed())))
    }

    @Test
    fun displayCompletedTask() {
        repository.saveTaskBlocking(Task("Title1", "Description1", isCompleted = true))

        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText("Active")).perform(click())
        onView(withText("Title1")).check(matches(IsNot.not(isDisplayed())))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText("Completed")).perform(click())
        onView(withText("Title1")).check(matches(isDisplayed()))
    }

    @Test
    fun showAllTasks() {
        repository.saveTaskBlocking(Task("Title1", "Description1"))
        repository.saveTaskBlocking(Task("Title2", "Description2", isCompleted = true))

        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText("All")).perform(click())
        onView(withText("Title1")).check(matches(isDisplayed()))
        onView(withText("Title2")).check(matches(isDisplayed()))
    }

    private fun launchActivity(): ActivityScenario<TasksActivity> {
        val activityScenario = launch(TasksActivity::class.java)
        activityScenario.onActivity { activity ->
            (activity.findViewById(R.id.tasks_list) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }
}