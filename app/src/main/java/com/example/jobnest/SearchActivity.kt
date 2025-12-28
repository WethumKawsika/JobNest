package com.example.jobnest

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class SearchActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var searchEditText: EditText
    private lateinit var jobRecyclerView: RecyclerView
    private lateinit var filterSalary: Button
    private lateinit var filterType: Button
    private lateinit var filterLocation: Button
    private lateinit var filterTime: Button
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var emptyStateView: LinearLayout

    private lateinit var database: DatabaseReference
    private var jobList = mutableListOf<Job>()
    private var filteredList = mutableListOf<Job>()
    private lateinit var jobAdapter: JobAdapter

    private var activeSalaryFilter: String? = null
    private var activeTypeFilter: String? = null
    private var activeLocationFilter: String? = null
    private var activeTimeFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.searchEditText)
        jobRecyclerView = findViewById(R.id.jobRecyclerView)
        filterSalary = findViewById(R.id.filterSalary)
        filterType = findViewById(R.id.filterType)
        filterLocation = findViewById(R.id.filterLocation)
        filterTime = findViewById(R.id.filterTime)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        emptyStateView = findViewById(R.id.emptyStateView)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Set current navigation item
        bottomNavigation.selectedItemId = R.id.nav_search

        // Setup toolbar back button
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Setup RecyclerView
        jobRecyclerView.layoutManager = LinearLayoutManager(this)
        jobAdapter = JobAdapter(filteredList)
        jobRecyclerView.adapter = jobAdapter

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference("jobs")

        // Load jobs from Firebase
        loadJobs()

        // Setup search functionality
        setupSearch()

        // Setup filter buttons
        setupFilters()

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun loadJobs() {
        // Show loading
        loadingIndicator.visibility = View.VISIBLE
        jobRecyclerView.visibility = View.GONE
        emptyStateView.visibility = View.GONE

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                jobList.clear()
                for (jobSnapshot in snapshot.children) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    if (job != null) {
                        jobList.add(job)
                    }
                }
                applyFilters()
            }

            override fun onCancelled(error: DatabaseError) {
                // Hide loading on error
                loadingIndicator.visibility = View.GONE
                emptyStateView.visibility = View.VISIBLE
                Toast.makeText(this@SearchActivity,
                    "Failed to load jobs", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                applyFilters()
            }
        })
    }

    private fun setupFilters() {
        filterSalary.setOnClickListener { showSalaryFilterDialog() }
        filterType.setOnClickListener { showJobTypeFilterDialog() }
        filterLocation.setOnClickListener { showLocationFilterDialog() }
        filterTime.setOnClickListener { showTimeFilterDialog() }
    }

    private fun applyFilters() {
        val query = searchEditText.text.toString()

        filteredList.clear()
        for (job in jobList) {
            val matchesQuery = query.isEmpty() ||
                    job.title.contains(query, ignoreCase = true) ||
                    job.type.contains(query, ignoreCase = true) ||
                    job.location.contains(query, ignoreCase = true)

            val matchesSalary = activeSalaryFilter == null || job.salary.contains(activeSalaryFilter!!, ignoreCase = true)
            val matchesType = activeTypeFilter == null || job.type.equals(activeTypeFilter, ignoreCase = true)
            val matchesLocation = activeLocationFilter == null || job.location.contains(activeLocationFilter!!, ignoreCase = true)
            val matchesTime = activeTimeFilter == null || job.time.contains(activeTimeFilter!!, ignoreCase = true)

            if (matchesQuery && matchesSalary && matchesType && matchesLocation && matchesTime) {
                filteredList.add(job)
            }
        }

        // Show/hide empty state and loading indicator
        loadingIndicator.visibility = View.GONE
        if (filteredList.isEmpty()) {
            emptyStateView.visibility = View.VISIBLE
            jobRecyclerView.visibility = View.GONE
        } else {
            emptyStateView.visibility = View.GONE
            jobRecyclerView.visibility = View.VISIBLE
        }

        jobAdapter.notifyDataSetChanged()
    }

    private fun showSalaryFilterDialog() {
        val salaryRanges = arrayOf(
            "All Salaries",
            "Rs. 500-1000",
            "Rs. 1000-2000",
            "Rs. 2000-3000",
            "Rs. 3000-5000",
            "Rs. 5000+"
        )

        android.app.AlertDialog.Builder(this)
            .setTitle("Select Salary Range")
            .setItems(salaryRanges) { dialog, which ->
                activeSalaryFilter = if (which == 0) null else salaryRanges[which].substring(4)
                applyFilters()
                updateFilterButtonStates()
                dialog.dismiss()
            }
            .show()
    }

    private fun showJobTypeFilterDialog() {
        val jobTypes = arrayOf(
            "All Types",
            "Promotion",
            "Tuition",
            "Delivery",
            "Part-time",
            "Sales",
            "Other"
        )

        android.app.AlertDialog.Builder(this)
            .setTitle("Select Job Type")
            .setItems(jobTypes) { dialog, which ->
                activeTypeFilter = if (which == 0) null else jobTypes[which]
                applyFilters()
                updateFilterButtonStates()
                dialog.dismiss()
            }
            .show()
    }

    private fun showLocationFilterDialog() {
        val locations = arrayOf(
            "All Locations",
            "Colombo",
            "Kandy",
            "Galle",
            "Negombo",
            "Jaffna",
            "Kurunegala",
            "Matara",
            "Anuradhapura"
        )

        android.app.AlertDialog.Builder(this)
            .setTitle("Select Location")
            .setItems(locations) { dialog, which ->
                activeLocationFilter = if (which == 0) null else locations[which]
                applyFilters()
                updateFilterButtonStates()
                dialog.dismiss()
            }
            .show()
    }

    private fun showTimeFilterDialog() {
        val times = arrayOf(
            "All Times",
            "Morning",
            "Evening",
            "Night",
            "Flexible"
        )

        android.app.AlertDialog.Builder(this)
            .setTitle("Select Work Time")
            .setItems(times) { dialog, which ->
                activeTimeFilter = if (which == 0) null else times[which]
                applyFilters()
                updateFilterButtonStates()
                dialog.dismiss()
            }
            .show()
    }

    private fun updateFilterButtonStates() {
        filterSalary.isSelected = activeSalaryFilter != null
        filterType.isSelected = activeTypeFilter != null
        filterLocation.isSelected = activeLocationFilter != null
        filterTime.isSelected = activeTimeFilter != null
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home - Coming Soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_search -> {
                    // Already on search screen
                    true
                }
                R.id.nav_saved -> {
                    startActivity(Intent(this, SavedActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
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
        // Ensure correct item is selected when returning to this activity
        bottomNavigation.selectedItemId = R.id.nav_search
    }
}