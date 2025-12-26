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
        // Filter by salary - will implement later
        filterSalary.setOnClickListener {
            // TODO: Show dialog to select salary range
        }

        // Filter by type
        filterType.setOnClickListener {
            // TODO: Show dialog to select job type
        }

        // Filter by location
        filterLocation.setOnClickListener {
            // TODO: Show dialog to select location
        }

        // Filter by time
        filterTime.setOnClickListener {
            // TODO: Show dialog to select time
        }
    }
}