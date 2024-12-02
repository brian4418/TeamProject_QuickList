package com.cs407.teamproject_quicklist

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settings)

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        setSupportActionBar(toolbar)

        // Change the toolbar title to "Settings"
        supportActionBar?.title = "Settings"
        // Or if you want to remove the default title and use the TextView from the layout:
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set up notification settings click listener
        val notificationLayout = findViewById<LinearLayout>(R.id.notification_settings_layout)
        notificationLayout.setOnClickListener {
            showNotificationSettingsDialog()
        }
    }

    private fun showNotificationSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_notification_settings, null)

        // Load saved preferences
        loadSavedPreferences(dialogView)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                // Save notification preferences
                saveNotificationPreferences(dialogView)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loadSavedPreferences(view: View) {
        val sharedPrefs = getSharedPreferences("notification_preferences", MODE_PRIVATE)

        view.findViewById<CheckBox>(R.id.checkbox_due_date).isChecked =
            sharedPrefs.getBoolean("due_date", false)
        view.findViewById<CheckBox>(R.id.checkbox_daily_summary).isChecked =
            sharedPrefs.getBoolean("daily_summary", false)
        view.findViewById<CheckBox>(R.id.checkbox_priority).isChecked =
            sharedPrefs.getBoolean("priority", false)
        view.findViewById<CheckBox>(R.id.checkbox_shared_tasks).isChecked =
            sharedPrefs.getBoolean("shared_tasks", false)
        view.findViewById<CheckBox>(R.id.checkbox_completion).isChecked =
            sharedPrefs.getBoolean("completion", false)
    }

    private fun saveNotificationPreferences(view: View) {
        val sharedPrefs = getSharedPreferences("notification_preferences", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean("due_date", view.findViewById<CheckBox>(R.id.checkbox_due_date).isChecked)
            putBoolean("daily_summary", view.findViewById<CheckBox>(R.id.checkbox_daily_summary).isChecked)
            putBoolean("priority", view.findViewById<CheckBox>(R.id.checkbox_priority).isChecked)
            putBoolean("shared_tasks", view.findViewById<CheckBox>(R.id.checkbox_shared_tasks).isChecked)
            putBoolean("completion", view.findViewById<CheckBox>(R.id.checkbox_completion).isChecked)
            apply()
        }
    }
}