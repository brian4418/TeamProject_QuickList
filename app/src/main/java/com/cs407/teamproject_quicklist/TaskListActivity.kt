package com.cs407.teamproject_quicklist

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.teamproject_quicklist.adapters.TaskAdapter
import com.cs407.teamproject_quicklist.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

class TaskListActivity : AppCompatActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    private lateinit var auth: FirebaseAuth

    companion object {
        const val ADD_TASK_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasklist)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.task_recycler_view)
        taskAdapter = TaskAdapter(emptyList())
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add dividers between items
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)

        // Observe tasks from ViewModel
        taskViewModel.tasks.observe(this, Observer { taskList ->
            taskAdapter.updateTasks(taskList)
        })

        // Fetch tasks
        taskViewModel.fetchTasks()

        // Navigate to AddTaskActivity
        val addTaskImageView: ImageView = findViewById(R.id.add_task_imageview)
        addTaskImageView.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }

        // Handle Search
        val searchTaskImageView: ImageView = findViewById(R.id.search_task_imageview)
        searchTaskImageView.setOnClickListener {
            showSearchDialog()
        }

        // Handle Logout
        /*
        val logoutImageView: ImageView = findViewById(R.id.logout_imageview)
        logoutImageView.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
         */
    }

    private fun showSearchDialog() {
        val searchEditText = EditText(this)
        searchEditText.hint = "Search tasks"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Search")
            .setView(searchEditText)
            .setPositiveButton("Search") { _, _ ->
                val query = searchEditText.text.toString()
                taskAdapter.filter(query)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    // Handle result from AddTaskActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            val newTask = data?.getSerializableExtra("new_task") as? com.cs407.teamproject_quicklist.model.Task
            newTask?.let {
                taskViewModel.addTask(it)
            }
        }
    }
}
