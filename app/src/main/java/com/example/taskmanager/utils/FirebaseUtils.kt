package com.example.taskmanager.utils

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtils {
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun getTaskCollection() = firestoreInstance.collection("tasks")
}
