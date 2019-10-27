package com.duzi.arcitecturesample

import androidx.annotation.VisibleForTesting
import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.TasksRepository

/**
 * ServiceLocator 에서 repository 를 생성할 때 context 가 필요하므로
 * context 를 안쓰고도 테스트를 할 수 있는 Repository 를 하나 만든다.
 * 테스트를 쉽게 하기 위한 용도
 */
class FakeRepositoryForTest: TasksRepository {

    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        return Result.Success(tasksServiceData.values.toList())
    }

    override suspend fun saveTask(task: Task) {
        tasksServiceData[task.id] = task
    }

    override suspend fun deleteAllTasks() {
        tasksServiceData.clear()
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.id)
        tasksServiceData[task.id] = completedTask
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, false, task.id)
        tasksServiceData[task.id] = activeTask
    }

    override suspend fun clearCompletedTasks() {
        tasksServiceData = tasksServiceData.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    @VisibleForTesting  // 메서드를 테스트하기 쉽게 만들기 위해 메서드의 가시성을 일반적으로 필요한 수준보다 더 높게 만들어준다
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
    }

}