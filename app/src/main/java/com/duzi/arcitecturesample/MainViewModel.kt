package com.duzi.arcitecturesample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.TasksRepository

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

    fun openTask(taskId: String) {
        _openTaskEvent.value = Event(taskId)
    }

    fun completeTask(task: Task, completed: Boolean) {
        if (completed) {
            // respository  completeTask
        } else {
            // respository  activateTask
        }

        loadTasks(false)
    }

    fun loadTasks(forceUpdate: Boolean) {
        _dataLoading.value = true

        // get task from network or local

        _dataLoading.value = false
    }


    fun refresh() {
        loadTasks(true)
    }

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }



}