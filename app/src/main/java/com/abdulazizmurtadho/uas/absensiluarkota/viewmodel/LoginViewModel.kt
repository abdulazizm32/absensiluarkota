package com.abdulazizmurtadho.uas.absensiluarkota.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdulazizmurtadho.uas.absensiluarkota.AbsenDao
import com.abdulazizmurtadho.uas.absensiluarkota.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModelProvider

class LoginViewModel(private val absenDao: AbsenDao) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun login(username: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
//                try {
//                    val users = absenDao.getAllUsers()
//                    Log.d("USERS_DEBUG", "Total users: ${users.size}")
//                    users.forEach { user ->
//                        Log.d("USERS_DEBUG", "User: ${user.username} | ${user.nama} | ${user.role}")
//                    }
//                } catch (e: Exception) {
//                    Log.e("USERS_DEBUG", "Error: ${e.message}")
//                }

                val user = absenDao.login(username, password)
                Log.d("LOGIN", "User found: ${user?.username} role: ${user?.role}")
                if (user != null) {
                    saveUserPrefs(user, context)
                    _loginState.value = LoginUiState.Success(user.nama, user.role == "admin")
                } else {
                    _loginState.value = LoginUiState.Error("Username/password salah")
                }
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error("Error: ${e.message}")
            }
        }
    }

    private fun saveUserPrefs(user: User, context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("username", user.username)
            putString("nama", user.nama)
            putString("role", user.role)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }
}

class LoginViewModelFactory(private val absenDao: AbsenDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(absenDao) as T
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val nama: String, val isAdmin: Boolean) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
