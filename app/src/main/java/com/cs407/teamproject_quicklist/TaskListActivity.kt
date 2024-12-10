package com.cs407.teamproject_quicklist

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.teamproject_quicklist.adapters.TaskAdapter
import com.cs407.teamproject_quicklist.model.Task
import com.cs407.teamproject_quicklist.viewmodel.TaskViewModel
import com.cs407.teamproject_quicklist.viewmodel.TaskViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class TaskListActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var auth: FirebaseAuth

    companion object {
        const val ADD_TASK_REQUEST_CODE = 100
        const val EDIT_TASK_REQUEST_CODE = 101
    }

    // Initialize the ViewModel with a factory
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(this)
    }

    // Request notification permission
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasklist)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find the more options ImageView
        val moreOptionsImage = findViewById<ImageView>(R.id.more_option_imageview)

        // Set click listener for the more options button
        moreOptionsImage.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // Check notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.task_recycler_view)
        taskAdapter = TaskAdapter(mutableListOf(), ::onTaskChecked)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add dividers between items
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)

        // Enable swipe-to-edit
        enableSwipeToEdit(recyclerView)

        // Observe tasks from ViewModel
        taskViewModel.tasks.observe(this, Observer { taskList ->
            Log.d("TaskListActivity", "Tasks updated: ${taskList.size}")
            taskList.forEach { Log.d("TaskListActivity", "Task: ${it.title}, Completed: ${it.isComplete}") }
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
    }

    private fun enableSwipeToEdit(recyclerView: RecyclerView) {
        val swipeToEditCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No move support
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Get the swiped task
                val position = viewHolder.adapterPosition
                val taskToEdit = taskAdapter.getTaskAtPosition(position)

                // Open AddTaskActivity with the task details pre-filled
                val intent = Intent(this@TaskListActivity, AddTaskActivity::class.java)
                intent.putExtra("edit_task", taskToEdit)
                startActivityForResult(intent, EDIT_TASK_REQUEST_CODE)

                // Reset the swipe (so the UI goes back to normal)
                taskAdapter.notifyItemChanged(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToEditCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
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

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.more_options_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    // Launch Settings Activity
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_logout -> {
                    // Handle Logout
                    auth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun onTaskChecked(task: Task, isChecked: Boolean) {
        Log.d("TaskListActivity", "Task ${task.title} marked as ${if (isChecked) "complete" else "incomplete"}")
        if (task.isComplete != isChecked) {
            task.isComplete = isChecked
            taskViewModel.markTaskComplete(task.id, isChecked)
            taskViewModel.fetchTasks() // Refresh the task list
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            val newTask = data?.getSerializableExtra("new_task") as? Task
            newTask?.let {
                taskViewModel.addTask(it)
            }
        } else if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            val editedTask = data?.getSerializableExtra("edited_task") as? Task
            editedTask?.let {
                taskViewModel.updateTask(it)
            }

            // Handle deleted task
            val deletedTaskId = data?.getStringExtra("deleted_task_id")
            deletedTaskId?.let {
                taskViewModel.deleteTask(it)
            }
        }
    }
}
