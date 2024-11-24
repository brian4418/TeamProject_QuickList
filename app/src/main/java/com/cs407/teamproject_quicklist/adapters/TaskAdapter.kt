package com.cs407.teamproject_quicklist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cs407.teamproject_quicklist.R
import com.cs407.teamproject_quicklist.model.Task

class TaskAdapter(private var tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var filteredTasks: List<Task> = tasks

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.task_title)
        val priorityTextView: TextView = view.findViewById(R.id.task_priority)
        val dateTextView: TextView = view.findViewById(R.id.task_date)
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

        // Set priority text and color
        holder.priorityTextView.text = task.priority
        val priorityColor = when (task.priority) {
            "High" -> android.graphics.Color.RED
            "Medium" -> android.graphics.Color.BLUE
            "Low" -> android.graphics.Color.GREEN
            else -> android.graphics.Color.BLACK
        }
        holder.priorityTextView.setTextColor(priorityColor)
    }

    override fun getItemCount(): Int = filteredTasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        filteredTasks = newTasks
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredTasks = if (query.isEmpty()) {
            tasks
        } else {
            tasks.filter { it.title.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}
