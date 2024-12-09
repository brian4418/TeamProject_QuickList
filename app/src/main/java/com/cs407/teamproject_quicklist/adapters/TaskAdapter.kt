package com.cs407.teamproject_quicklist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.teamproject_quicklist.R
import com.cs407.teamproject_quicklist.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(
    private var tasks: MutableList<Task>,
    private val onTaskCompleteChange: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var filteredTasks: MutableList<Task> = tasks

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.task_title)
        val priorityTextView: TextView = view.findViewById(R.id.task_priority)
        val dateTextView: TextView = view.findViewById(R.id.task_date)
        val completeCheckBox: CheckBox = view.findViewById(R.id.task_complete_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = filteredTasks[position]

        holder.titleTextView.text = task.title
        holder.dateTextView.text = task.deadline
        holder.completeCheckBox.setOnCheckedChangeListener(null)
        holder.completeCheckBox.isChecked = task.isComplete

        // Set priority text and color
        holder.priorityTextView.text = task.priority
        val priorityColor = when (task.priority) {
            "High" -> android.graphics.Color.RED
            "Medium" -> android.graphics.Color.BLUE
            "Low" -> android.graphics.Color.GREEN
            else -> android.graphics.Color.BLACK
        }
        holder.priorityTextView.setTextColor(priorityColor)

        // Add listener for checkbox changes
        holder.completeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            onTaskCompleteChange(task, isChecked)
        }
    }

    override fun getItemCount(): Int = filteredTasks.size

    fun getTaskAtPosition(position: Int): Task {
        return filteredTasks[position]
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks.toMutableList()
        filteredTasks = tasks
        notifyDataSetChanged()
    }

    fun updateSingleTask(task: Task) {
        val index = filteredTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            filteredTasks[index] = task
            notifyItemChanged(index)
        }
    }

    fun filter(query: String) {
        filteredTasks = if (query.isEmpty()) {
            tasks.toMutableList()
        } else {
            tasks.filter { it.title.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    // Add the getCurrentTasks function
    fun getCurrentTasks(): List<Task> {
        return filteredTasks
    }
}
