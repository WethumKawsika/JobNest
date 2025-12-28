package com.example.jobnest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class JobAdapter(private val jobList: List<Job>) :
    RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val jobTitle: TextView = view.findViewById(R.id.jobTitle)
        val jobType: TextView = view.findViewById(R.id.jobType)
        val jobSalary: TextView = view.findViewById(R.id.jobSalary)
        val jobLocation: TextView = view.findViewById(R.id.jobLocation)
        val jobTime: TextView = view.findViewById(R.id.jobTime)
        val saveButton: ImageButton = view.findViewById(R.id.saveButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        val context = holder.itemView.context

        holder.jobTitle.text = job.title
        holder.jobType.text = job.type
        holder.jobSalary.text = job.salary
        holder.jobLocation.text = job.location
        holder.jobTime.text = job.time

        // Update save button icon based on saved state
        updateSaveButtonIcon(holder.saveButton, job.id, context)

        // Handle save button click
        holder.saveButton.setOnClickListener {
            toggleSaveJob(job.id, context)
            updateSaveButtonIcon(holder.saveButton, job.id, context)
        }

        holder.itemView.setOnClickListener {
            JobDetailsActivity.start(context, job)
        }
    }

    override fun getItemCount() = jobList.size

    private fun toggleSaveJob(jobId: String, context: Context) {
        val prefs = context.getSharedPreferences("JobNestPrefs", Context.MODE_PRIVATE)
        val savedJobs = prefs.getStringSet("saved_jobs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        if (savedJobs.contains(jobId)) {
            savedJobs.remove(jobId)
            Toast.makeText(context, "Job removed from saved", Toast.LENGTH_SHORT).show()
        } else {
            savedJobs.add(jobId)
            Toast.makeText(context, "Job saved!", Toast.LENGTH_SHORT).show()
        }

        prefs.edit().putStringSet("saved_jobs", savedJobs).apply()
    }

    private fun updateSaveButtonIcon(button: ImageButton, jobId: String, context: Context) {
        val prefs = context.getSharedPreferences("JobNestPrefs", Context.MODE_PRIVATE)
        val savedJobs = prefs.getStringSet("saved_jobs", emptySet()) ?: emptySet()

        if (savedJobs.contains(jobId)) {
            button.setImageResource(android.R.drawable.btn_star_big_on) // Filled star
        } else {
            button.setImageResource(android.R.drawable.btn_star_big_off) // Empty star
        }
    }
}