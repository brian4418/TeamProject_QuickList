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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.teamproject_quicklist.adapters.TaskAdapter
import com.cs407.teamproject_quicklist.model.Task
import com.cs407.teamproject_quicklist.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

class TaskListActivity : AppCompatActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    private lateinit var auth: FirebaseAuth

    companion object {
        const val ADD_TASK_REQUEST_CODE = 100
        const val EDIT_TASK_REQUEST_CODE = 101
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

        // Enable swipe-to-edit
        enableSwipeToEdit(recyclerView)

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

    // Handle result from AddTaskActivity
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
