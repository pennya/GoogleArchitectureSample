package com.duzi.arcitecturesample

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.TasksFilterType
import com.duzi.arcitecturesample.data.source.TasksRepository
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(private val repository: TasksRepository): ViewModel() {

    private val _items = MutableLiveData<List<Task>>().apply { value = emptyList() }
    val items: LiveData<List<Task>> = _items

    // task open observer
    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>> = _openTaskEvent

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noTasksLabel = MutableLiveData<Int>()
    val noTasksLabel: LiveData<Int> = _noTasksLabel

    private val _noTaskIconRes = MutableLiveData<Int>()
    val noTaskIconRes: LiveData<Int> = _noTaskIconRes

    private var _currentFiltering = TasksFilterType.ALL_TASKS

    init {
        setFiltering(TasksFilterType.ALL_TASKS)
    }

    fun openTask(taskId: String) {
        _openTaskEvent.value = Event(taskId)
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            repository.completeTask(task)
        } else {
            repository.activateTask(task)
        }

        loadTasks(false)
    }

    fun loadTasks(forceUpdate: Boolean) {
        _dataLoading.value = true

        viewModelScope.launch {
            val tasksResult = repository.getTasks(forceUpdate)
            if (tasksResult is Result.Success) {
                val tasks = tasksResult.data

                val tasksToShow = ArrayList<Task>()
                for (task in tasks) {
                    when (_currentFiltering) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                        TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                            tasksToShow.add(task)
                        }
                        TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                            tasksToShow.add(task)
                        }
                    }
                }

                _items.value = ArrayList(tasksToShow)
            } else {
                _items.value = emptyList()
            }

            _dataLoading.value = false
        }
    }


    fun refresh() {
        loadTasks(true)
    }

    fun setFiltering(requestType: TasksFilterType) {
        _currentFiltering = requestType

        when (requestType) {
            TasksFilterType.ALL_TASKS -> {
                setFilter(R.string.label_all,
                    R.string.no_tasks_all,
                    R.drawable.logo_no_fill)
            }
            TasksFilterType.ACTIVE_TASKS -> {
                setFilter(
                    R.string.label_active, R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp
                )
            }
            TasksFilterType.COMPLETED_TASKS -> {
                setFilter(
                    R.string.label_completed, R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_96dp
                )
            }
        }
    }

    private fun setFilter(
        @StringRes filteringLabelString: Int,
        @StringRes noTasksLabelString: Int,
        @DrawableRes noTaskIconDrawable: Int
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noTasksLabel.value = noTasksLabelString
        _noTaskIconRes.value = noTaskIconDrawable
    }

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            repository.deleteAllTasks()
        }
    }

}