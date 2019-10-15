package com.duzi.arcitecturesample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.duzi.arcitecturesample.data.TasksRepository

class MainViewModel(private val repository: TasksRepository): ViewModel() {

    // task open observer
    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>> = _openTaskEvent
}