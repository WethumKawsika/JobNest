package com.example.jobnest // This will be YOUR package name

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import android.widget.Toast

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var jobRecyclerView: RecyclerView
    private lateinit var filterSalary: Button
    private lateinit var filterType: Button
    private lateinit var filterLocation: Button
    private lateinit var filterTime: Button

    private lateinit var database: DatabaseReference
    private var jobList = mutableListOf<Job>()
    private var filteredList = mutableListOf<Job>()
    private lateinit var jobAdapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText)
        jobRecyclerView = findViewById(R.id.jobRecyclerView)
        filterSalary = findViewById(R.id.filterSalary)
        filterType = findViewById(R.id.filterType)
        filterLocation = findViewById(R.id.filterLocation)
        filterTime = findViewById(R.id.filterTime)

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
    }

    private fun loadJobs() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                jobList.clear()
                for (jobSnapshot in snapshot.children) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    if (job != null) {
                        jobList.add(job)
                    }
                }
                filteredList.clear()
                filteredList.addAll(jobList)
                jobAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterJobs(s.toString())
            }
        })
    }

    private fun filterJobs(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(jobList)
        } else {
            for (job in jobList) {
                if (job.title.contains(query, ignoreCase = true) ||
                    job.type.contains(query, ignoreCase = true) ||
                    job.location.contains(query, ignoreCase = true)) {
                    filteredList.add(job)
                }
            }
        }
        jobAdapter.notifyDataSetChanged()
    }

    private fun setupFilters() {
        // Filter by salary
        filterSalary.setOnClickListener {
            showSalaryFilterDialog()
        }

        // Filter by type
        filterType.setOnClickListener {
            showJobTypeFilterDialog()
        }

        // Filter by location
        filterLocation.setOnClickListener {
            showLocationFilterDialog()
        }

        // Filter by time
        filterTime.setOnClickListener {
            showTimeFilterDialog()
        }

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
                when (which) {
                    0 -> filterBySalary(null) // All salaries
                    1 -> filterBySalary("500-1000")
                    2 -> filterBySalary("1000-2000")
                    3 -> filterBySalary("2000-3000")
                    4 -> filterBySalary("3000-5000")
                    5 -> filterBySalary("5000+")
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun filterBySalary(range: String?) {
        filteredList.clear()

        if (range == null) {
            // Show all jobs
            filteredList.addAll(jobList)
        } else {
            for (job in jobList) {
                // Check if job salary contains the range
                if (job.salary.contains(range, ignoreCase = true)) {
                    filteredList.add(job)
                }
            }
        }

        jobAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Filtered by salary: ${range ?: "All"}", Toast.LENGTH_SHORT).show()
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
                val selectedType = if (which == 0) null else jobTypes[which]
                filterByJobType(selectedType)
                dialog.dismiss()
            }
            .show()
    }

    private fun filterByJobType(type: String?) {
        filteredList.clear()

        if (type == null) {
            // Show all jobs
            filteredList.addAll(jobList)
        } else {
            for (job in jobList) {
                if (job.type.equals(type, ignoreCase = true)) {
                    filteredList.add(job)
                }
            }
        }

        jobAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Filtered by type: ${type ?: "All"}", Toast.LENGTH_SHORT).show()
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
                val selectedLocation = if (which == 0) null else locations[which]
                filterByLocation(selectedLocation)
                dialog.dismiss()
            }
            .show()
    }

    private fun filterByLocation(location: String?) {
        filteredList.clear()

        if (location == null) {
            // Show all jobs
            filteredList.addAll(jobList)
        } else {
            for (job in jobList) {
                if (job.location.contains(location, ignoreCase = true)) {
                    filteredList.add(job)
                }
            }
        }

        jobAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Filtered by location: ${location ?: "All"}", Toast.LENGTH_SHORT).show()
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
                val selectedTime = if (which == 0) null else times[which]
                filterByTime(selectedTime)
                dialog.dismiss()
            }
            .show()
    }

    private fun filterByTime(time: String?) {
        filteredList.clear()

        if (time == null) {
            // Show all jobs
            filteredList.addAll(jobList)
        } else {
            for (job in jobList) {
                if (job.time.contains(time, ignoreCase = true)) {
                    filteredList.add(job)
                }
            }
        }

        jobAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Filtered by time: ${time ?: "All"}", Toast.LENGTH_SHORT).show()
    }
}