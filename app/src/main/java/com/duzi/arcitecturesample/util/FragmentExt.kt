package com.duzi.arcitecturesample.util

import androidx.fragment.app.Fragment
import com.duzi.arcitecturesample.MyApplication
import com.duzi.arcitecturesample.ViewModelFactory

fun Fragment.getViewModelFactory(): ViewModelFactory {
    // 컴파일 시점에 Fake or Mock RemoteRepository 가 결정된다.
    val repository = (requireContext() as MyApplication).taskRepository
    return ViewModelFactory(repository)
}