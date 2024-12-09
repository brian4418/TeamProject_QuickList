package com.cs407.teamproject_quicklist.repository

import android.content.Context
import androidx.work.*
import com.cs407.teamproject_quicklist.model.Task
import com.cs407.teamproject_quicklist.workers.NotificationWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class TaskRepository(private val context: Context) {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun getUserTasksReference(): DatabaseReference {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            throw IllegalStateException("User is not authenticated")
        }
        return database.child("tasks").child(userId)
    }

    fun addTask(task: Task) {
        val userTasksRef = getUserTasksReference()
        val taskId = userTasksRef.push().key
        taskId?.let {
            task.id = it
            userTasksRef.child(it).setValue(task)
                .addOnSuccessListener {
                    scheduleTaskReminder(task)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    fun editTask(taskId: String, task: Task) {
        val userTasksRef = getUserTasksReference()
        userTasksRef.child(taskId).setValue(task)
            .addOnSuccessListener {
                scheduleTaskReminder(task)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun deleteTask(taskId: String) {
        val userTasksRef = getUserTasksReference()
        userTasksRef.child(taskId).removeValue()
            .addOnSuccessListener {
                cancelTaskReminder(taskId)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun scheduleTaskReminder(task: Task) {
        try {
            val taskDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(task.deadline)
            val taskTimeMillis = taskDate?.time ?: throw IllegalArgumentException("Invalid date format")
            val currentTimeMillis = System.currentTimeMillis()
            val delay = taskTimeMillis - currentTimeMillis

            if (delay > 0) {
                val data = Data.Builder()
                    .putString("taskTitle", task.title)
                    .putString("taskDescription", task.description)
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag(task.id) // Add tag for cancellation
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
                println("Scheduled notification for task: ${task.title} with delay: $delay ms")
            } else {
                println("Task deadline already passed. Skipping notification for: ${task.title}")
            }
        } catch (e: Exception) {
            println("Error scheduling reminder: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun cancelTaskReminder(taskId: String) {
        try {
            WorkManager.getInstance(context).cancelAllWorkByTag(taskId)
            println("Cancelled notification for task ID: $taskId")
        } catch (e: Exception) {
            println("Error cancelling reminder for task ID: $taskId: ${e.message}")
            e.printStackTrace()
        }
    }

    fun getAllTasks(callback: (List<Task>) -> Unit) {
        val userTasksRef = getUserTasksReference()
        userTasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = snapshot.children.mapNotNull { it.getValue(Task::class.java) }
                callback(taskList)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
                callback(emptyList())
            }
        })
    }

    fun markTaskComplete(taskId: String, isComplete: Boolean) {
        val userTasksRef = getUserTasksReference()
        userTasksRef.child(taskId).child("isComplete").setValue(isComplete)
            .addOnSuccessListener {
                if (!isComplete) {
                    userTasksRef.child(taskId).get().addOnSuccessListener { snapshot ->
                        val task = snapshot.getValue(Task::class.java)
                        task?.let { scheduleTaskReminder(it) }
                    }
                } else {
                    cancelTaskReminder(taskId)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
