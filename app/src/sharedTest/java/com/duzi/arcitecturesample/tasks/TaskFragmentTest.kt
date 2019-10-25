package com.duzi.arcitecturesample.tasks

import com.duzi.arcitecturesample.FakeRepositoryForTest
import com.duzi.arcitecturesample.ServiceLocator
import com.duzi.arcitecturesample.data.source.TasksRepository
import org.junit.Before

class TaskFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun setup() {
        repository = FakeRepositoryForTest()
        ServiceLocator.tasksRepository = repository
    }

    
}