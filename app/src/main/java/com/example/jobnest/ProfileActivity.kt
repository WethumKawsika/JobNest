package com.example.jobnest // Change to YOUR package name

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var universityEditText: TextInputEditText
    private lateinit var editButton: Button
    private lateinit var saveButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        profileImage = findViewById(R.id.profileImage)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        universityEditText = findViewById(R.id.universityEditText)
        editButton = findViewById(R.id.editButton)
        saveButton = findViewById(R.id.saveButton)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        // Load user data
        loadUserData()

        // Setup buttons
        editButton.setOnClickListener {
            enableEditMode()
        }

        saveButton.setOnClickListener {
            saveUserData()
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    nameEditText.setText(user.name)
                    emailEditText.setText(user.email)
                    phoneEditText.setText(user.phone)
                    universityEditText.setText(user.university)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity,
                    "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun enableEditMode() {
        isEditMode = true
        nameEditText.isEnabled = true
        phoneEditText.isEnabled = true
        universityEditText.isEnabled = true

        editButton.visibility = Button.GONE
        saveButton.visibility = Button.VISIBLE
    }

    private fun saveUserData() {
        val userId = auth.currentUser?.uid ?: return

        val name = nameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val university = universityEditText.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || university.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "phone" to phone,
            "university" to university
        )

        database.child(userId).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                disableEditMode()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun disableEditMode() {
        isEditMode = false
        nameEditText.isEnabled = false
        phoneEditText.isEnabled = false
        universityEditText.isEnabled = false

        editButton.visibility = Button.VISIBLE
        saveButton.visibility = Button.GONE
    }
}