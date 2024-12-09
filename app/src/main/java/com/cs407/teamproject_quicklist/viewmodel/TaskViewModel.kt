package com.cs407.teamproject_quicklist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.teamproject_quicklist.model.Task
import com.cs407.teamproject_quicklist.repository.TaskRepository

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    fun fetchTasks() {
        repository.getAllTasks { taskList ->
            _tasks.postValue(taskList)
        }
    }

    fun addTask(task: Task) {
        repository.addTask(task)
    }

    fun updateTask(task: Task) {
        repository.editTask(task.id, task)
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
    }

    fun markTaskComplete(taskId: String, isComplete: Boolean) {
        repository.markTaskComplete(taskId, isComplete)
    }
}
