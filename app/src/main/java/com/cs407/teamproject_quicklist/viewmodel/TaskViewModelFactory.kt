package com.cs407.teamproject_quicklist.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cs407.teamproject_quicklist.repository.TaskRepository

class TaskViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            val repository = TaskRepository(context)
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
