package com.cs407.teamproject_quicklist

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cs407.teamproject_quicklist.model.Task
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var deadlineEditText: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var submitButton: Button

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask)

        titleEditText = findViewById(R.id.task_title_edittext)
        deadlineEditText = findViewById(R.id.deadline_edittext)
        prioritySpinner = findViewById(R.id.priority_spinner)
        submitButton = findViewById(R.id.task_submit_button)

        // Set up the deadline picker
        deadlineEditText.setOnClickListener {
            showDatePicker()
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

        // Handle the submit button click
        submitButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val deadline = deadlineEditText.text.toString()
            val priority = prioritySpinner.selectedItem.toString()

            if (title.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            } else {
                val newTask = Task(
                    id = "",
                    title = title,
                    deadline = deadline,
                    description = "",
                    priority = priority
                )
                val resultIntent = Intent()
                resultIntent.putExtra("new_task", newTask)
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
}
