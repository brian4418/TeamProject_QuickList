package com.cs407.teamproject_quicklist

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settings)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        setSupportActionBar(toolbar)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set up notification settings click listener
        val notificationLayout = findViewById<LinearLayout>(R.id.notification_settings_layout)
        notificationLayout.setOnClickListener {
            showNotificationSettingsDialog()
        }

        // Set up account settings click listener
        val accountLayout = findViewById<LinearLayout>(R.id.account_settings_layout)
        accountLayout.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    // Handle back button click
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val currentPasswordEdit = dialogView.findViewById<EditText>(R.id.currentPasswordEditText)
        val newPasswordEdit = dialogView.findViewById<EditText>(R.id.newPasswordEditText)
        val confirmPasswordEdit = dialogView.findViewById<EditText>(R.id.confirmPasswordEditText)

        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { dialog, _ ->
                val currentPassword = currentPasswordEdit.text.toString()
                val newPassword = newPasswordEdit.text.toString()
                val confirmPassword = confirmPasswordEdit.text.toString()

                if (newPassword.isEmpty() || currentPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "New passwords don't match", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                changePassword(currentPassword, newPassword)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        if (user?.email == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Re-authenticate user before changing password
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                // Authenticated successfully, now change password
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update password: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
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