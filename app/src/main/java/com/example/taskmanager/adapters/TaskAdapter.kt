package com.example.taskmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.models.Task

class TaskAdapter(
    private val tasks: MutableList<Task> = mutableListOf(),
    private val onItemClick: (Task) -> Unit,
    private val onEditClick: (Task) -> Unit, // Callback for edit button click
    private val onDeleteClick: (Task) -> Unit // New callback for delete button click
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    // ViewHolder for Task items
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        private val taskDueDateTextView: TextView = itemView.findViewById(R.id.taskDueDateTextView)
        private val editTaskButton: Button = itemView.findViewById(R.id.editTaskButton)
        private val deleteTaskButton: Button = itemView.findViewById(R.id.deleteTaskButton) // Delete button

        // Bind task data to the item view
        fun bind(task: Task) {
            taskNameTextView.text = task.name
            taskDueDateTextView.text = task.dueDate

            // Set click listener for the item
            itemView.setOnClickListener {
                onItemClick(task)
            }

            // Set click listener for the Edit button
            editTaskButton.setOnClickListener {
                onEditClick(task) // Trigger the edit action
            }

            // Set click listener for the Delete button
            deleteTaskButton.setOnClickListener {
                onDeleteClick(task) // Trigger the delete action
            }
        }
    }
    // Inflate item_task layout and return ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }
    // Bind ViewHolder with task data
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }
    // Return total number of items
    override fun getItemCount(): Int = tasks.size
    
    // Update the task list and notify changes
    fun submitList(newTasks: List<Task>) {
        if (tasks != newTasks) {
            tasks.clear()
            tasks.addAll(newTasks)
            notifyDataSetChanged()
        }
    }
}
