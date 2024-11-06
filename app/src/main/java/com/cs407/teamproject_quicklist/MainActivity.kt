package com.cs407.teamproject_quicklist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize buttons and set click listeners
        val buttonWeeklyTasks: Button = findViewById(R.id.buttonWeeklyTasks)
        val buttonMonthlyTasks: Button = findViewById(R.id.buttonMonthlyTasks)

        buttonWeeklyTasks.setOnClickListener {
            val intent = Intent(this, WeeklyTasksActivity::class.java)
            startActivity(intent)
        }

        buttonMonthlyTasks.setOnClickListener {
            val intent = Intent(this, MonthlyTasksActivity::class.java)
            startActivity(intent)
        }
    }
}
