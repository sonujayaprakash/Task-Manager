package com.example.taskmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.adapters.TaskAdapter
import com.example.taskmanager.models.Task
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private lateinit var firestore: FirebaseFirestore
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        firestore = FirebaseFirestore.getInstance()

        val addTaskButton = findViewById<Button>(R.id.addTaskButton)
        val profileButton = findViewById<Button>(R.id.profileButton)
        val taskRecyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)

        // Initialize TaskAdapter with item click, edit button click, and delete button click listeners
        taskAdapter = TaskAdapter(
            onItemClick = { task ->
                // Handle task item click for viewing details or any other purpose
                val intent = Intent(this, TaskDetailsActivity::class.java)
                intent.putExtra("taskId", task.id)
                startActivity(intent)
            },
            onEditClick = { task ->
                // Handle edit button click for editing the task
                val intent = Intent(this, EditTaskActivity::class.java)
                intent.putExtra("taskId", task.id)
                startActivity(intent)
            },
            onDeleteClick = { task ->
                // Handle delete button click for deleting the task
                deleteTask(task)
            }
        )

        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        addTaskButton.setOnClickListener {
            // Navigate to AddTaskActivity to create new tasks
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        profileButton.setOnClickListener {
            // Navigate to ProfileActivity to view/edit the profile
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Fetch tasks from Firestore
        fetchTasks()
    }

    private fun fetchTasks() {
        // Using Firestore's real-time listener to fetch tasks
        firestore.collection("tasks")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    // Handle error if any
                    return@addSnapshotListener
                }

                val tasks = querySnapshot?.documents?.map { document ->
                    Task(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        dueDate = document.getString("dueDate") ?: "",
                        status = document.getString("status") ?: "Pending"
                    )
                } ?: emptyList()

                // Submit the task list to the adapter
                taskAdapter.submitList(tasks)
            }
    }

    // Method to delete a task
    private fun deleteTask(task: Task) {
        val taskRef = firestore.collection("tasks").document(task.id)
        taskRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
