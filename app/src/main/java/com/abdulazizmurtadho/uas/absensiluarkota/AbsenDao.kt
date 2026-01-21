package com.abdulazizmurtadho.uas.absensiluarkota

import androidx.room.*

@Dao
interface AbsenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(absen: Absen)

    @Query("SELECT * FROM absensi ORDER BY id DESC")
    fun getAll(): List<Absen> // Flow untuk real-time update

    @Query("SELECT COUNT(*) FROM absensi")
    suspend fun getCount(): Int

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}