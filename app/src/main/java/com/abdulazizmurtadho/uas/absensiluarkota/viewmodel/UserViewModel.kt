package com.abdulazizmurtadho.uas.absensiluarkota.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdulazizmurtadho.uas.absensiluarkota.FirstApp
import com.abdulazizmurtadho.uas.absensiluarkota.User
import com.abdulazizmurtadho.uas.absensiluarkota.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class UserViewModel(private val userDao: UserDao) : ViewModel() {

    companion object {
        fun Factory(app: FirstApp): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return UserViewModel(app.getUserDao()) as T
                }
            }

        val PEGAWAI_ROLES = listOf("pegawai")
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private var currentUser = User()

    init {
        viewModelScope.launch {
            userDao.getAllUsers().collect { userList ->
                _users.value = userList
            }
        }
    }

    fun setCurrentUser(user: User) {
        currentUser = user
    }

    fun onNamaChange(nama: String) {
        currentUser = currentUser.copy(nama = nama)  // Match field name
    }

    fun onUsernameChange(username: String) {
        currentUser = currentUser.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        currentUser = currentUser.copy(password = password)
    }

    fun saveUser() {
        viewModelScope.launch {
            userDao.insertUser(currentUser)
            Log.d("UserDb", "User saved: ${currentUser.nama}")
        }
    }

    fun updateUser() {
        viewModelScope.launch {
            userDao.updateUser(currentUser)
            Log.d("UserDb", "User updated: ${currentUser.nama}")
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userDao.deleteUser(user)
            Log.d("UserDb", "User deleted: ${user.nama}")
        }
    }
}
