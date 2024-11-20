package com.cs407.teamproject_quicklist

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask)

        // Handle Submit Button Click
        val taskSubmitButton: Button = findViewById(R.id.task_submit_button)
        taskSubmitButton.setOnClickListener {
            // Finish the current activity and return to the previous one
            finish()
        }
    }
}