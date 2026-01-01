package com.example.jobnest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// -------------------- DATA CLASSES --------------------

data class UserData(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val userType: String? = null // nullable for first-time Google login
)

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val currentUserData: UserData? = null
)

// -------------------- VIEWMODEL --------------------

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    // -------------------- AUTH STATE CHECK --------------------

    private fun checkAuthState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserData(currentUser.uid)
        } else {
            _authState.value = AuthState(isAuthenticated = false)
        }
    }

    private fun fetchUserData(uid: String) {
        viewModelScope.launch {
            try {
                _authState.update { it.copy(isLoading = true) }

                val userRef = firestore.collection("users").document(uid)
                val document = userRef.get().await()

                if (document.exists()) {
                    val userData = UserData(
                        uid = uid,
                        email = document.getString("email") ?: "",
                        fullName = document.getString("fullName") ?: "",
                        phoneNumber = document.getString("phoneNumber") ?: "",
                        address = document.getString("address") ?: "",
                        userType = document.getString("userType")
                    )

                    _authState.value = AuthState(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUserData = userData
                    )
                } else {
                    // If no Firestore document exists for this user (possible if created via console or missing during sign up),
                    // create a default user document so that email sign-in proceeds into the app.
                    val currentUser = auth.currentUser
                    val email = currentUser?.email ?: ""

                    val defaultMap = hashMapOf(
                        "uid" to uid,
                        "email" to email,
                        "fullName" to (currentUser?.displayName ?: ""),
                        "phoneNumber" to "",
                        "address" to "",
                        "userType" to "student",
                        "createdAt" to System.currentTimeMillis()
                    )

                    // Persist default user document
                    userRef.set(defaultMap).await()

                    val userData = UserData(
                        uid = uid,
                        email = email,
                        fullName = currentUser?.displayName ?: "",
                        phoneNumber = "",
                        address = "",
                        userType = "student"
                    )

                    _authState.value = AuthState(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUserData = userData
                    )
                }
            } catch (e: Exception) {
                // If fetching user data failed but Firebase auth has a currentUser, allow the app
                // to proceed (user is authenticated) with a minimal UserData. This helps when
                // Firestore reads fail transiently but authentication succeeded.
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val fallbackUser = UserData(
                        uid = currentUser.uid,
                        email = currentUser.email ?: "",
                        fullName = currentUser.displayName ?: "",
                        phoneNumber = "",
                        address = "",
                        userType = null
                    )

                    _authState.value = AuthState(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUserData = fallbackUser,
                        error = "Failed to fetch full profile: ${e.message}"
                    )
                } else {
                    _authState.value = AuthState(
                        isLoading = false,
                        isAuthenticated = false,
                        error = e.message
                    )
                }
            }
        }
    }

    // -------------------- EMAIL AUTH --------------------

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.update { it.copy(isLoading = true, error = null) }

                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let {
                    fetchUserData(it.uid)
                } ?: run {
                    _authState.value = AuthState(
                        isLoading = false,
                        isAuthenticated = false,
                        error = "Sign in failed"
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState(
                    isLoading = false,
                    isAuthenticated = false,
                    error = e.message
                )
            }
        }
    }

    fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        address: String,
        userType: String
    ) {
        viewModelScope.launch {
            try {
                _authState.update { it.copy(isLoading = true, error = null) }

                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user ?: return@launch

                val userMap = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "fullName" to fullName,
                    "phoneNumber" to phoneNumber,
                    "address" to address,
                    "userType" to userType,
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(user.uid)
                    .set(userMap)
                    .await()

                fetchUserData(user.uid)

            } catch (e: Exception) {
                _authState.value = AuthState(
                    isLoading = false,
                    isAuthenticated = false,
                    error = e.message
                )
            }
        }
    }

    // -------------------- GOOGLE SIGN IN --------------------

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _authState.update { it.copy(isLoading = true, error = null) }

                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user ?: return@launch

                val userRef = firestore.collection("users").document(user.uid)
                val document = userRef.get().await()

                if (!document.exists()) {
                    val newUser = UserData(
                        uid = user.uid,
                        email = user.email ?: "",
                        fullName = user.displayName ?: "",
                        userType = null // Will be set after popup
                    )

                    userRef.set(newUser).await()

                    _authState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            currentUserData = newUser
                        )
                    }
                } else {
                    fetchUserData(user.uid)
                }

            } catch (e: Exception) {
                _authState.update {
                    it.copy(
                        isLoading = false,
                        error = "Google Sign-In failed: ${e.message}"
                    )
                }
            }
        }
    }

    // -------------------- UPDATE USER ROLE --------------------

    fun updateUserRole(userType: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch

                firestore.collection("users")
                    .document(currentUser.uid)
                    .update("userType", userType)
                    .await()

                _authState.update { state ->
                    state.copy(
                        currentUserData = state.currentUserData?.copy(
                            userType = userType
                        )
                    )
                }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(error = "Failed to update role: ${e.message}")
                }
            }
        }
    }

    // -------------------- PROFILE UPDATE --------------------

    fun updateProfile(
        fullName: String,
        phoneNumber: String,
        address: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true) }

            try {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    onError("User not logged in")
                    _authState.update { it.copy(isLoading = false) }
                    return@launch
                }

                val updates = hashMapOf(
                    "fullName" to fullName,
                    "phoneNumber" to phoneNumber,
                    "address" to address
                )

                firestore.collection("users")
                    .document(currentUser.uid)
                    .update(updates as Map<String, Any>)
                    .await()

                _authState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        currentUserData = currentState.currentUserData?.copy(
                            fullName = fullName,
                            phoneNumber = phoneNumber,
                            address = address
                        )
                    )
                }

                onSuccess()

            } catch (e: Exception) {
                _authState.update { it.copy(isLoading = false) }
                onError(e.message ?: "Failed to update profile")
            }
        }
    }

    // -------------------- PASSWORD RESET --------------------

    fun resetPassword(
        email: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _authState.update { it.copy(isLoading = true, error = null) }

                // Send password reset email
                auth.sendPasswordResetEmail(email).await()

                // Update state on success
                _authState.update { it.copy(isLoading = false, error = null) }
                onSuccess()

            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("no user record", ignoreCase = true) == true ->
                        "No account found with this email"
                    e.message?.contains("invalid-email", ignoreCase = true) == true ->
                        "Invalid email format"
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Network error. Check your connection"
                    else -> e.message ?: "Failed to send reset email"
                }

                _authState.update {
                    it.copy(isLoading = false, error = errorMessage)
                }
                onError(errorMessage)
            }
        }
    }

    // -------------------- LOGOUT --------------------

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState(
            isAuthenticated = false,
            currentUserData = null
        )
    }

    fun clearError() {
        _authState.update { it.copy(error = null) }
    }
}
