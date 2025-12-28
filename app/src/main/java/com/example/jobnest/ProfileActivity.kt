package com.example.jobnest

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream

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
    private var selectedImageBitmap: Bitmap? = null

    // Activity result launchers
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            selectedImageBitmap = it
            profileImage.setImageBitmap(it)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                selectedImageBitmap = bitmap
                profileImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

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

        // Setup profile image click
        profileImage.setOnClickListener {
            showImagePickerDialog()
        }

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

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        AlertDialog.Builder(this)
            .setTitle("Change Profile Picture")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> checkStoragePermissionAndOpen()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkStoragePermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                else -> {
                    storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        } else {
            // Older Android versions use READ_EXTERNAL_STORAGE
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                else -> {
                    storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun openCamera() {
        cameraLauncher.launch(null)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
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

                    // Load profile picture from SharedPreferences (local storage)
                    loadProfilePicture()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity,
                    "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadProfilePicture() {
        val prefs = getSharedPreferences("JobNestPrefs", Context.MODE_PRIVATE)
        val imageString = prefs.getString("profile_picture", null)

        if (imageString != null) {
            try {
                val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                profileImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                // Failed to load, keep default image
            }
        }
    }

    private fun saveProfilePicture() {
        selectedImageBitmap?.let { bitmap ->
            try {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val imageBytes = outputStream.toByteArray()
                val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                val prefs = getSharedPreferences("JobNestPrefs", Context.MODE_PRIVATE)
                prefs.edit().putString("profile_picture", imageString).apply()

                Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to save picture", Toast.LENGTH_SHORT).show()
            }
        }
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

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "phone" to phone,
            "university" to university
        )

        database.child(userId).updateChildren(updates)
            .addOnSuccessListener {
                // Save profile picture locally
                saveProfilePicture()

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
                    startActivity(Intent(this, SavedActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
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
        bottomNavigation.selectedItemId = R.id.nav_profile
    }
}