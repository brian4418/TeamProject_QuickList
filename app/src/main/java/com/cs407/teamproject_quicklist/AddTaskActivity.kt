package com.cs407.teamproject_quicklist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cs407.teamproject_quicklist.model.Task
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var deadlineEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var recurringSpinner: Spinner
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
    private val calendar = Calendar.getInstance()

    private var isEditMode = false
    private var taskId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask)

        // Set up toolbar with back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (intent.hasExtra("edit_task")) "Edit Task" else "Add Task"

        titleEditText = findViewById(R.id.task_title_edittext)
        deadlineEditText = findViewById(R.id.deadline_edittext)
        timeEditText = findViewById(R.id.time_edittext)
        prioritySpinner = findViewById(R.id.priority_spinner)
        recurringSpinner = findViewById(R.id.recurring_spinner)
        submitButton = findViewById(R.id.task_submit_button)
        deleteButton = findViewById(R.id.task_delete_button)

        // Check if we're in edit mode
        val taskToEdit = intent.getSerializableExtra("edit_task") as? Task
        if (taskToEdit != null) {
            isEditMode = true
            taskId = taskToEdit.id

            // Pre-fill fields with task details
            titleEditText.setText(taskToEdit.title)

            val deadlineParts = taskToEdit.deadline.split(" ")
            if (deadlineParts.size == 2) {
                deadlineEditText.setText(deadlineParts[0]) // Date
                timeEditText.setText(deadlineParts[1])    // Time
                // Set calendar with the existing values
                val taskDate = dateFormat.parse(deadlineParts[0])
                val taskTime = timeFormat.parse(deadlineParts[1])
                if (taskDate != null) {
                    calendar.time = taskDate
                }
                if (taskTime != null) {
                    val taskTimeCalendar = Calendar.getInstance()
                    taskTimeCalendar.time = taskTime
                    calendar.set(Calendar.HOUR_OF_DAY, taskTimeCalendar.get(Calendar.HOUR_OF_DAY))
                    calendar.set(Calendar.MINUTE, taskTimeCalendar.get(Calendar.MINUTE))
                }
            }

            prioritySpinner.setSelection(
                when (taskToEdit.priority) {
                    "High" -> 0
                    "Medium" -> 1
                    "Low" -> 2
                    else -> 0
                }
            )

            recurringSpinner.setSelection(
                when (taskToEdit.recurring) {
                    "Daily" -> 1
                    "Weekly" -> 2
                    "Monthly" -> 3
                    else -> 0
                }
            )

            // Show delete button if editing
            deleteButton.visibility = View.VISIBLE

            // Set delete button action
            deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(taskToEdit)
            }
        }

        // Set up the deadline picker
        deadlineEditText.setOnClickListener {
            showDatePicker()
        }

        // Set up the time picker
        timeEditText.setOnClickListener {
            showTimePicker()
        }

        // Set up the priority spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.priority_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            prioritySpinner.adapter = adapter
        }

        // Set up the recurring spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.recurring_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            recurringSpinner.adapter = adapter
        }

        // Handle the submit button click
        submitButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val deadlineDate = deadlineEditText.text.toString()
            val deadlineTime = timeEditText.text.toString()
            val priority = prioritySpinner.selectedItem.toString()
            val recurring = recurringSpinner.selectedItem.toString()

            if (title.isEmpty() || deadlineDate.isEmpty() || deadlineTime.isEmpty()) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            } else {
                val deadline = "$deadlineDate $deadlineTime" // Combine date and time
                val updatedTask = Task(
                    id = if (isEditMode) taskId else "", // Preserve task ID if editing
                    title = title,
                    deadline = deadline,
                    description = "", // Placeholder for now
                    priority = priority,
                    recurring = recurring
                )
                val resultIntent = Intent()
                resultIntent.putExtra(
                    if (isEditMode) "edited_task" else "new_task",
                    updatedTask
                )
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val dateString = dateFormat.format(calendar.time)
                deadlineEditText.setText(dateString)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    @SuppressLint("DefaultLocale")
    private fun showTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                val timeString = timeFormat.format(calendar.time)
                timeEditText.setText(timeString)
            },
            hour,
            minute,
            false // Use 12-hour format with AM/PM
        )
        timePickerDialog.show()
    }

    private fun showDeleteConfirmationDialog(task: Task) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                deleteTask(task)
            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()
    }

    private fun deleteTask(task: Task) {
        val resultIntent = Intent()
        resultIntent.putExtra("deleted_task_id", task.id)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
