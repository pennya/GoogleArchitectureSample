package com.duzi.arcitecturesample

import android.view.Gravity
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.TasksRepository
import com.duzi.arcitecturesample.util.DataBindingIdlingResource
import com.duzi.arcitecturesample.util.EspressoIdlingResource
import com.duzi.arcitecturesample.util.monitorActivity
import com.duzi.arcitecturesample.util.saveTaskBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {
    private lateinit var tasksRepository: TasksRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        tasksRepository = ServiceLocator.provideTasksRepository(getApplicationContext())
    }

    @After
    fun finish() {
        ServiceLocator.resetRepository()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun drawerNavigationFromTasksToStatistics() {
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        Espresso.onView(ViewMatchers.withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.statisticsFragment))

        Espresso.onView(ViewMatchers.withId(R.id.statistics_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.START)))
            .perform(DrawerActions.open())

        Espresso.onView(ViewMatchers.withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.taskFragment))

        Espresso.onView(ViewMatchers.withId(R.id.tasks_container_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun tasksScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.START)))

        Espresso.onView(
            ViewMatchers.withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isOpen(Gravity.START)))
    }

    @Test
    fun statsScreen_clickOnAndroidHomeIcon_OpensNavigation() {
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        activityScenario.onActivity {
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.statisticsFragment)
        }

        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.START))) // Left Drawer should be closed.

        Espresso.onView(
            ViewMatchers.withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isOpen(Gravity.START))) // Left drawer is open open.
    }

    @Test
    fun taskDetailScreen_doubleUIBackButton() {
        val task = Task("UI <- button", "Description")
        tasksRepository.saveTaskBlocking(task)

        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        Espresso.onView(ViewMatchers.withText("UI <- button")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.edit_task_fab)).perform(ViewActions.click())

        Espresso.onView(
            ViewMatchers.withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.task_detail_title_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(
            ViewMatchers.withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.tasks_container_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}