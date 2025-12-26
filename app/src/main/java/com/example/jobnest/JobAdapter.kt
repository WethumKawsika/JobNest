package com.example.jobnest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobAdapter(private val jobList: List<Job>) :
    RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val jobTitle: TextView = view.findViewById(R.id.jobTitle)
        val jobType: TextView = view.findViewById(R.id.jobType)
        val jobSalary: TextView = view.findViewById(R.id.jobSalary)
        val jobLocation: TextView = view.findViewById(R.id.jobLocation)
        val jobTime: TextView = view.findViewById(R.id.jobTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job, parent, false)

        // Add click effect
        view.setOnClickListener {
            // TODO: Open job details
        }

        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobList[position]
        holder.jobTitle.text = job.title
        holder.jobType.text = job.type
        holder.jobSalary.text = job.salary
        holder.jobLocation.text = job.location
        holder.jobTime.text = job.time
    }

    override fun getItemCount() = jobList.size
}