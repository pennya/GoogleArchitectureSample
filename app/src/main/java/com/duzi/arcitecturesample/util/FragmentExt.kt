package com.duzi.arcitecturesample.util

import androidx.fragment.app.Fragment
import com.duzi.arcitecturesample.MyApplication
import com.duzi.arcitecturesample.ViewModelFactory

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext() as MyApplication).repository
    return ViewModelFactory(repository)
}