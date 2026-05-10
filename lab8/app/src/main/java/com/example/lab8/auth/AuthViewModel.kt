package com.example.lab8.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
)

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    var uiState by mutableStateOf(
        AuthUiState(isAuthenticated = firebaseAuth.currentUser != null)
    )
        private set

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, errorMessage = null)
    }

    fun onConfirmPasswordChange(value: String) {
        uiState = uiState.copy(confirmPassword = value, errorMessage = null)
    }

    fun signIn() {
        val email = uiState.email.trim()
        val password = uiState.password

        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Email and password are required")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uiState = uiState.copy(isLoading = false, isAuthenticated = true, errorMessage = null)
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        errorMessage = mapFirebaseError(task.exception, defaultMessage = "Sign in failed")
                    )
                }
            }
    }

    fun signUp() {
        val email = uiState.email.trim()
        val password = uiState.password
        val confirmPassword = uiState.confirmPassword

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            uiState = uiState.copy(errorMessage = "Email and password are required")
            return
        }

        if (password.length < 6) {
            uiState = uiState.copy(errorMessage = "Password must be at least 6 characters")
            return
        }

        if (password != confirmPassword) {
            uiState = uiState.copy(errorMessage = "Passwords do not match")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uiState = uiState.copy(isLoading = false, isAuthenticated = true, errorMessage = null)
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        errorMessage = mapFirebaseError(task.exception, defaultMessage = "Sign up failed")
                    )
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
        uiState = AuthUiState()
    }

    private fun mapFirebaseError(throwable: Throwable?, defaultMessage: String): String {
        val errorCode = (throwable as? FirebaseAuthException)?.errorCode
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "Email format is invalid"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email is already registered"
            "ERROR_WEAK_PASSWORD" -> "Password is too weak"
            "ERROR_INVALID_CREDENTIAL", "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND", "ERROR_INVALID_LOGIN_CREDENTIALS" ->
                "Email or password is incorrect"
            "ERROR_USER_DISABLED" -> "This account has been disabled"
            else -> throwable?.localizedMessage ?: defaultMessage
        }
    }
}

