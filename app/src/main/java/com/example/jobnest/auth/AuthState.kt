package com.example.jobnest.auth

import com.google.firebase.auth.FirebaseUser

data class AuthState(
    val currentUser: FirebaseUser? = null,
    val isLoading: Boolean = false,val error: String? = null
)
