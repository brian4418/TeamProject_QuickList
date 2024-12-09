package com.cs407.teamproject_quicklist.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cs407.teamproject_quicklist.R

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return try {
            // Retrieve task title and description from input data
            val taskTitle = inputData.getString("taskTitle") ?: "Task Reminder"
            val taskDescription = inputData.getString("taskDescription") ?: "Don't forget to complete your task!"

            // Create a notification channel
            createNotificationChannel()

            // Create the notification
            val notification = NotificationCompat.Builder(applicationContext, "TASK_REMINDER_CHANNEL")
                .setSmallIcon(R.drawable.wisconsin_logo) // Replace with your app's icon resource
                .setContentTitle(taskTitle)
                .setContentText(taskDescription)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            // Check for notification permission (Android 13+)
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("NotificationWorker", "Missing POST_NOTIFICATIONS permission")
                return Result.failure()
            }

            // Show the notification
            NotificationManagerCompat.from(applicationContext).notify(System.currentTimeMillis().toInt(), notification)

            Log.d("NotificationWorker", "Notification sent successfully for task: $taskTitle")
            Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error sending notification: ${e.message}", e)
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        val channelId = "TASK_REMINDER_CHANNEL"
        val channelName = "Task Reminders"
        val channelDescription = "Notifications for task reminders"

        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = channelDescription
        }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        Log.d("NotificationWorker", "Notification channel created: $channelId")
    }
}
