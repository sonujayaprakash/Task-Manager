package com.example.taskmanager

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanager.utils.FirebaseUtils
import com.google.firebase.firestore.DocumentSnapshot

class EditTaskActivity : AppCompatActivity() {

    private var taskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        val taskNameEditText = findViewById<EditText>(R.id.taskNameEditText)
        val taskDescriptionEditText = findViewById<EditText>(R.id.taskDescriptionEditText)
        val taskDueDateEditText = findViewById<EditText>(R.id.taskDueDateEditText)
        val saveTaskButton = findViewById<Button>(R.id.saveTaskButton)

        taskId = intent.getStringExtra("taskId")

        taskId?.let {
            // Fetch task data from Firestore and populate the edit fields
            FirebaseUtils
                .getTaskCollection()
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    populateFields(document, taskNameEditText, taskDescriptionEditText, taskDueDateEditText)
                }
        }

        saveTaskButton.setOnClickListener {
            val updatedTask: MutableMap<String, Any> = hashMapOf(
                "name" to taskNameEditText.text.toString(),
                "description" to taskDescriptionEditText.text.toString(),
                "dueDate" to taskDueDateEditText.text.toString()
            )

            taskId?.let {
                FirebaseUtils.getTaskCollection().document(it)
                    .update(updatedTask)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update task: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun populateFields(
        document: DocumentSnapshot,
        nameField: EditText,
        descField: EditText,
        dateField: EditText
    ) {
        nameField.setText(document.getString("name"))
        descField.setText(document.getString("description"))
        dateField.setText(document.getString("dueDate"))
    }
}
