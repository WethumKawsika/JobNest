package com.example.jobnest

import android.app.Application
import com.google.firebase.FirebaseApp

class JobNestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
