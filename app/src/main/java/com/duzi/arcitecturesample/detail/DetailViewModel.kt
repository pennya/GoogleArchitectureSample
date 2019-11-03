package com.duzi.arcitecturesample.detail

import androidx.lifecycle.*
import com.duzi.arcitecturesample.Event
import com.duzi.arcitecturesample.data.Result
import com.duzi.arcitecturesample.data.Task
import com.duzi.arcitecturesample.data.source.TasksRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val tasksRepository: TasksRepository): ViewModel() {

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _editTaskEvent = MutableLiveData<Event<Unit>>()
    val editTaskEvent: LiveData<Event<Unit>> = _editTaskEvent

    private val _deleteTaskEvent = MutableLiveData<Event<Unit>>()
    val deleteTaskEvent: LiveData<Event<Unit>> = _deleteTaskEvent

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val taskId: String?
        get() = _task.value?.id

    val completed: LiveData<Boolean> = Transformations.map(_task) { input: Task? ->
        input?.isCompleted ?: false
    }

    fun deleteTask() = viewModelScope.launch {
        taskId?.let {
            tasksRepository.deleteTask(it)
            _deleteTaskEvent.value = Event(Unit)
        }
    }

    fun editTask() {
        _editTaskEvent.value = Event(Unit)
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val task = _task.value ?: return@launch
        if (completed) {
            tasksRepository.completeTask(task)
            showSnackbarMessage("Task marked complete")
        } else {
            tasksRepository.activateTask(task)
            showSnackbarMessage("Task marked active")
        }
    }

    fun start(taskId: String?, forceRefresh: Boolean = false) {
        if (_isDataAvailable.value == true && !forceRefresh || _dataLoading.value == true) {
            return
        }

        _dataLoading.value = true

        viewModelScope.launch {
            if (taskId != null) {
                tasksRepository.getTask(taskId, false).let { result ->
                    if (result is Result.Success) {
                        onTaskLoaded(result.data)
                    } else {
                        onDataNotAvailable(result)
                    }
                }
            }
            _dataLoading.value = false
        }
    }

    private fun setTask(task: Task?) {
        this._task.value = task
        _isDataAvailable.value = task != null
    }

    private fun onTaskLoaded(task: Task) {
        setTask(task)
    }

    private fun onDataNotAvailable(result: Result<Task>) {
        _task.value = null
        _isDataAvailable.value = false
    }

    private fun showSnackbarMessage(message: String) {
        _snackbarText.value = Event(message)
    }

    fun refresh() {
        taskId?.let { start(it, true) }
    }
}