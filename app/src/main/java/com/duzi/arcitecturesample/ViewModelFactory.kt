package com.duzi.arcitecturesample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.duzi.arcitecturesample.data.source.TasksRepository
import com.duzi.arcitecturesample.detail.DetailViewModel

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val tasksRepository: TasksRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(tasksRepository)
                isAssignableFrom(DetailViewModel::class.java) ->
                    DetailViewModel(tasksRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
