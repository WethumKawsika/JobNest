package com.example.jobnest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var jobTitle: TextView
    private lateinit var jobSalary: TextView
    private lateinit var jobType: TextView
    private lateinit var jobLocation: TextView
    private lateinit var jobTime: TextView
    private lateinit var jobFood: TextView
    private lateinit var jobTransport: TextView
    private lateinit var jobRequiredPersons: TextView
    private lateinit var jobAgeLimit: TextView
    private lateinit var saveButton: ImageButton
    private lateinit var applyButton: Button

    private var currentJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        jobTitle = findViewById(R.id.jobTitle)
        jobSalary = findViewById(R.id.jobSalary)
        jobType = findViewById(R.id.jobType)
        jobLocation = findViewById(R.id.jobLocation)
        jobTime = findViewById(R.id.jobTime)
        jobFood = findViewById(R.id.jobFood)
        jobTransport = findViewById(R.id.jobTransport)
        jobRequiredPersons = findViewById(R.id.jobRequiredPersons)
        jobAgeLimit = findViewById(R.id.jobAgeLimit)
        saveButton = findViewById(R.id.saveButton)
        applyButton = findViewById(R.id.applyButton)

        // Setup toolbar
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Get job data from intent
        loadJobData()

        // Setup save button
        setupSaveButton()

        // Setup apply button
        applyButton.setOnClickListener {
            applyForJob()
        }
    }

    private fun loadJobData() {
        val jobId = intent.getStringExtra("JOB_ID") ?: ""
        val jobTitleStr = intent.getStringExtra("JOB_TITLE") ?: ""
        val jobTypeStr = intent.getStringExtra("JOB_TYPE") ?: ""
        val jobSalaryStr = intent.getStringExtra("JOB_SALARY") ?: ""
        val jobLocationStr = intent.getStringExtra("JOB_LOCATION") ?: ""
        val jobTimeStr = intent.getStringExtra("JOB_TIME") ?: ""
        val jobFoodStr = intent.getStringExtra("JOB_FOOD") ?: "Not specified"
        val jobTransportStr = intent.getStringExtra("JOB_TRANSPORT") ?: "Not specified"
        val jobRequiredPersonsStr = intent.getStringExtra("JOB_REQUIRED_PERSONS") ?: "Not specified"
        val jobAgeLimitStr = intent.getStringExtra("JOB_AGE_LIMIT") ?: "Not specified"

        currentJob = Job(
            id = jobId,
            title = jobTitleStr,
            type = jobTypeStr,
            salary = jobSalaryStr,
            location = jobLocationStr,
            time = jobTimeStr,
            food = jobFoodStr,
            transport = jobTransportStr,
            requiredPersons = jobRequiredPersonsStr,
            ageLimit = jobAgeLimitStr
        )

        // Display data
        jobTitle.text = jobTitleStr
        jobSalary.text = jobSalaryStr
        jobType.text = jobTypeStr
        jobLocation.text = jobLocationStr
        jobTime.text = jobTimeStr
        jobFood.text = if (jobFoodStr.isEmpty()) "Not provided" else jobFoodStr
        jobTransport.text = if (jobTransportStr.isEmpty()) "Not provided" else jobTransportStr
        jobRequiredPersons.text = if (jobRequiredPersonsStr.isEmpty()) "Not specified" else jobRequiredPersonsStr
        jobAgeLimit.text = if (jobAgeLimitStr.isEmpty()) "Any age" else jobAgeLimitStr
    }

    private fun setupSaveButton() {
        updateSaveButtonIcon()

        saveButton.setOnClickListener {
            toggleSaveJob()
            updateSaveButtonIcon()
        }
    }

    private fun toggleSaveJob() {
        val jobId = currentJob?.id ?: return
        val prefs = getSharedPreferences("JobNestPrefs", Context.MODE_PRIVATE)
        val savedJobs = prefs.getStringSet("saved_jobs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        if (savedJobs.contains(jobId)) {
            savedJobs.remove(jobId)
            Toast.makeText(this, "Job removed from saved", Toast.LENGTH_SHORT).show()
        } else {
            savedJobs.add(jobId)
            Toast.makeText(this, "Job saved!", Toast.LENGTH_SHORT).show()
        }

        prefs.edit().putStringSet("saved_jobs", savedJobs).apply()
    }

    private fun updateSaveButtonIcon() {
        val jobId = currentJob?.id ?: return
        val prefs = getSharedPreferences("JobNestPrefs", Context.MODE_PRIVATE)
        val savedJobs = prefs.getStringSet("saved_jobs", emptySet()) ?: emptySet()

        if (savedJobs.contains(jobId)) {
            saveButton.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            saveButton.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }

    private fun applyForJob() {
        // For now, just show contact options
        // In future, this could dial phone or open chat
        Toast.makeText(this, "Contact job owner - Feature coming soon!", Toast.LENGTH_LONG).show()

        // Optional: Open phone dialer
        // val intent = Intent(Intent.ACTION_DIAL)
        // intent.data = Uri.parse("tel:0771234567")
        // startActivity(intent)
    }

    companion object {
        fun start(context: Context, job: Job) {
            val intent = Intent(context, JobDetailsActivity::class.java).apply {
                putExtra("JOB_ID", job.id)
                putExtra("JOB_TITLE", job.title)
                putExtra("JOB_TYPE", job.type)
                putExtra("JOB_SALARY", job.salary)
                putExtra("JOB_LOCATION", job.location)
                putExtra("JOB_TIME", job.time)
                putExtra("JOB_FOOD", job.food)
                putExtra("JOB_TRANSPORT", job.transport)
                putExtra("JOB_REQUIRED_PERSONS", job.requiredPersons)
                putExtra("JOB_AGE_LIMIT", job.ageLimit)
            }
            context.startActivity(intent)
        }
    }
}