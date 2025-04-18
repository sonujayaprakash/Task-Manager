package com.example.taskmanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        val uploadImageButton = findViewById<Button>(R.id.uploadImageButton)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                profileImageView.setImageURI(uri)
            }
        }

        uploadImageButton.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        saveButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val name = nameEditText.text.toString()
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            if (name.isBlank()) {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                val ref = storage.reference.child("profiles/$userId.jpg")
                ref.putFile(selectedImageUri!!).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveUserProfile(userId, name, uri.toString())
                        progressBar.visibility = View.GONE
                    }
                }.addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                saveUserProfile(userId, name, null)
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun saveUserProfile(userId: String, name: String, profileImageUrl: String?) {
        val user = hashMapOf(
            "name" to name,
            "profileImageUrl" to profileImageUrl
        )
        firestore.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save profile: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
