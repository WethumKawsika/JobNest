package com.example.jobnest.repository

import android.util.Log
import com.example.jobnest.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        address: String,
        userType: String
    ): Result<User> {
        return try {
            Log.d("AuthRepository", "Starting signup process for: $email")

            // Create auth user
            Log.d("AuthRepository", "Creating Firebase Auth user...")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            Log.d("AuthRepository", "Firebase Auth user created with ID: $userId")

            // Create user document in Firestore
            val user = User(
                userId = userId,
                email = email,
                fullName = fullName,
                phoneNumber = phoneNumber,
                address = address,
                userType = userType
            )

            Log.d("AuthRepository", "Saving user data to Firestore...")
            db.collection("users").document(userId).set(user).await()
            Log.d("AuthRepository", "User data saved to Firestore successfully")

            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "SignUp error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            Log.d("AuthRepository", "Starting sign in for: $email")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User is null")
            Log.d("AuthRepository", "Sign in successful for: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "SignIn error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            Log.d("AuthRepository", "Starting Google sign in")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Google sign in failed: user is null")

            // Check if user is new
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                Log.d("AuthRepository", "New user from Google Sign In. Creating Firestore entry.")
                val user = User(
                    userId = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    fullName = firebaseUser.displayName ?: "User",
                    phoneNumber = "",
                    address = "",
                    userType = "Student" // Default user type for Google Sign In
                )
                db.collection("users").document(firebaseUser.uid).set(user).await()
                Log.d("AuthRepository", "New user saved to Firestore.")
            } else {
                Log.d("AuthRepository", "Existing user from Google Sign In.")
            }

            Log.d("AuthRepository", "Google sign in successful for: ${firebaseUser.email}")
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google SignIn error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            Log.d("AuthRepository", "Signing out user")
            auth.signOut()
            Log.d("AuthRepository", "Sign out successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "SignOut error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            Log.d("AuthRepository", "Sending password reset email to: $email")
            auth.sendPasswordResetEmail(email).await()
            Log.d("AuthRepository", "Password reset email sent")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Reset password error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getCurrentUserData(): Result<User?> {
        return try {
            val userId = currentUserId ?: return Result.success(null)
            Log.d("AuthRepository", "Loading user data for ID: $userId")
            val document = db.collection("users").document(userId).get().await()
            val user = document.toObject(User::class.java)
            Log.d("AuthRepository", "User data loaded: ${user?.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Get user data error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
        fullName: String,
        phoneNumber: String,
        address: String
    ): Result<Unit> {
        return try {
            val userId = currentUserId ?: throw Exception("User not logged in")
            Log.d("AuthRepository", "Updating profile for user: $userId")

            val updates = hashMapOf(
                "fullName" to fullName,
                "phoneNumber" to phoneNumber,
                "address" to address,
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection("users").document(userId).update(updates as Map<String, Any>).await()
            Log.d("AuthRepository", "Profile updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Update profile error: ${e.message}", e)
            Result.failure(e)
        }
    }
}