package com.example.jobnest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var profileImage: ImageView
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var universityEditText: TextInputEditText
    private lateinit var editButton: Button
    private lateinit var saveButton: Button
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        profileImage = findViewById(R.id.profileImage)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        universityEditText = findViewById(R.id.universityEditText)
        editButton = findViewById(R.id.editButton)
        saveButton = findViewById(R.id.saveButton)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Set current navigation item
        bottomNavigation.selectedItemId = R.id.nav_profile

        // Setup toolbar back button
        toolbar.setNavigationOnClickListener {
            finish()
        }

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

        // Setup bottom navigation
        setupBottomNavigation()
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

        editButton.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
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

        val user = User(name, auth.currentUser?.email ?: "", phone, university)

        database.child(userId).setValue(user)
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

        editButton.visibility = View.VISIBLE
        saveButton.visibility = View.GONE
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home - Coming Soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_saved -> {
                    Toast.makeText(this, "Saved - Coming Soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    // Already on profile screen
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure correct item is selected when returning to this activity
        bottomNavigation.selectedItemId = R.id.nav_profile
    }
}
