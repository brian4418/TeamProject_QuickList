package com.cs407.teamproject_quicklist.repository

import android.util.Log
import com.cs407.teamproject_quicklist.model.Task
import com.google.firebase.database.*

class TaskRepository {

    private val database = FirebaseDatabase.getInstance().reference

    fun addTask(task: Task) {
        val taskId = database.child("tasks").push().key
        taskId?.let {
            task.id = it
            database.child("tasks").child(it).setValue(task)
                .addOnSuccessListener {
                    Log.d("TaskRepository", "Task added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("TaskRepository", "Error adding task", e)
                }
        }
    }

    fun editTask(taskId: String, task: Task) {
        database.child("tasks").child(taskId).setValue(task)
            .addOnSuccessListener {
                Log.d("TaskRepository", "Task edited successfully")
            }
            .addOnFailureListener { e ->
                Log.e("TaskRepository", "Error editing task", e)
            }
    }

    fun deleteTask(taskId: String) {
        database.child("tasks").child(taskId).removeValue()
            .addOnSuccessListener {
                Log.d("TaskRepository", "Task deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("TaskRepository", "Error deleting task", e)
            }
    }

    fun getAllTasks(callback: (List<Task>) -> Unit) {
        database.child("tasks").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let { taskList.add(it) }
                }
                callback(taskList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TaskRepository", "Error fetching tasks", error.toException())
                callback(emptyList()) // Return an empty list or handle the error as needed
            }
        })
    }
}
