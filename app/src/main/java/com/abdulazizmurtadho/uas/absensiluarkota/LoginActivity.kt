package com.abdulazizmurtadho.uas.absensiluarkota

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = AppDatabase.getDatabase(this)

        prefs = getSharedPreferences("user_session", MODE_PRIVATE)

        // Cek kalau sudah login
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username & password wajib!", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = db.absenDao().login(username, password)
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        // Simpan session
                        prefs.edit().apply {
                            putString("username", user.username)
                            putString("nama", user.nama)
                            putString("role", user.role)
                            putBoolean("isLoggedIn", true)
                            apply()
                        }

                        Toast.makeText(this@LoginActivity, "Login sukses: ${user.nama}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Username/password salah!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Ada Kesalahan , SIlakan diperbaiki", Toast.LENGTH_LONG).show()
                    Log.e("error_log", "Gagal simpan foto: ${e.message}")
                }
            }
        }
    }
}

