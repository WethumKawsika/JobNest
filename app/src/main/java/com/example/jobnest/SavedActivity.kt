package com.example.jobnest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class SavedActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var savedJobsRecyclerView: RecyclerView
    private lateinit var emptyStateView: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView

    private var savedJobsList = mutableListOf<Job>()
    private lateinit var jobAdapter: JobAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        savedJobsRecyclerView = findViewById(R.id.savedJobsRecyclerView)
        emptyStateView = findViewById(R.id.emptyStateView)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Set current navigation item
        bottomNavigation.selectedItemId = R.id.nav_saved

        // Setup RecyclerView
        savedJobsRecyclerView.layoutManager = LinearLayoutManager(this)
        jobAdapter = JobAdapter(savedJobsList)
        savedJobsRecyclerView.adapter = jobAdapter

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference("jobs")

        // Load saved jobs
        loadSavedJobs()

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun loadSavedJobs() {
        val savedJobIds = getSavedJobIds()

        if (savedJobIds.isEmpty()) {
            showEmptyState()
            return
        }

        // Fetch jobs from Firebase
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                savedJobsList.clear()
                for (jobSnapshot in snapshot.children) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    if (job != null && savedJobIds.contains(job.id)) {
                        savedJobsList.add(job)
                    }
                }

                if (savedJobsList.isEmpty()) {
                    showEmptyState()
                } else {
                    emptyStateView.visibility = View.GONE
                    savedJobsRecyclerView.visibility = View.VISIBLE
                    jobAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SavedActivity,
                    "Failed to load saved jobs", Toast.LENGTH_SHORT).show()
                showEmptyState()
            }
        })
    }

    private fun showEmptyState() {
        emptyStateView.visibility = View.VISIBLE
        savedJobsRecyclerView.visibility = View.GONE
    }

    private fun getSavedJobIds(): Set<String> {
        val prefs = getSharedPreferences("JobNestPrefs", Context.MODE_PRIVATE)
        return prefs.getStringSet("saved_jobs", emptySet()) ?: emptySet()
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
                    // Already on saved screen
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.nav_saved
        loadSavedJobs() // Refresh when returning
    }
}