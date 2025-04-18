package com.example.taskmanager

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanager.models.Task
import com.google.firebase.firestore.FirebaseFirestore

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var taskId: String
    private lateinit var taskNameTextView: TextView
    private lateinit var taskDescriptionTextView: TextView
    private lateinit var taskDueDateTextView: TextView
    private lateinit var taskStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        firestore = FirebaseFirestore.getInstance()

        taskNameTextView = findViewById(R.id.taskNameTextView)
        taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView)
        taskDueDateTextView = findViewById(R.id.taskDueDateTextView)
        taskStatusTextView = findViewById(R.id.taskStatusTextView)

        taskId = intent.getStringExtra("taskId") ?: return

        // Fetch task details from Firestore
        fetchTaskDetails()
    }

    private fun fetchTaskDetails() {
        firestore.collection("tasks").document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val task = Task(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        dueDate = document.getString("dueDate") ?: "",
                        status = document.getString("status") ?: "Pending"
                    )
                    // Populate the views with task details
                    taskNameTextView.text = task.name
                    taskDescriptionTextView.text = task.description
                    taskDueDateTextView.text = task.dueDate
                    taskStatusTextView.text = task.status
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}
