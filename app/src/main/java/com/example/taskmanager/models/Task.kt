package com.example.taskmanager.models

data class Task(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val dueDate: String = "",
    val status: String = "Pending"
)
