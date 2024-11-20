package com.cs407.teamproject_quicklist

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TaskListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasklist)

        // Navigate to Add Task Screen
        val addTaskImageView: ImageView = findViewById(R.id.add_task_imageview)
        addTaskImageView.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }
}