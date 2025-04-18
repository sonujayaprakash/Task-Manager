package com.example.taskmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddTaskActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        firestore = FirebaseFirestore.getInstance()

        val taskNameEditText = findViewById<EditText>(R.id.taskNameEditText)
        val taskDescriptionEditText = findViewById<EditText>(R.id.taskDescriptionEditText)
        val taskDueDateEditText = findViewById<EditText>(R.id.taskDueDateEditText)
        val saveTaskButton = findViewById<Button>(R.id.saveTaskButton)

        saveTaskButton.setOnClickListener {
            val taskName = taskNameEditText.text.toString()
            val taskDescription = taskDescriptionEditText.text.toString()
            val taskDueDate = taskDueDateEditText.text.toString()

            // Input validation
            if (taskName.isEmpty() || taskDescription.isEmpty() || taskDueDate.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                val task = hashMapOf(
                    "name" to taskName,
                    "description" to taskDescription,
                    "dueDate" to taskDueDate,
                    "status" to "Pending"
                )

                // Adding task to Firestore
                firestore.collection("tasks")
                    .add(task)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(this, "Task Added Successfully", Toast.LENGTH_SHORT).show()

                        // Redirecting to DashboardActivity
                        val intent = Intent(this, DashboardActivity::class.java)
                        // Optionally, passing the task ID or entire task
                        intent.putExtra("newTaskId", documentReference.id)  // Passing the new task ID to the Dashboard
                        startActivity(intent)

                        // Finish AddTaskActivity so user can't navigate back to it
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to add task: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
