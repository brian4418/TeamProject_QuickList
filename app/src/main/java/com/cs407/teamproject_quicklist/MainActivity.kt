package com.cs407.teamproject_quicklist

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Change the toolbar title to "Settings"
        supportActionBar?.title = "Settings"
        // Or if you want to remove the default title and use the TextView from the layout:
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}