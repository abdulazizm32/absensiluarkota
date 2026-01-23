package com.abdulazizmurtadho.uas.absensiluarkota

import androidx.room.*

@Dao
interface AbsenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(absen: Absen): Long

    @Query("SELECT * FROM absensi ORDER BY tanggal DESC")
    suspend fun getAll(): List<Absen>;

    @Query("SELECT COUNT(*) FROM absensi")
    suspend fun getCount(): Int

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Transaction
    suspend fun ensureTestUsers() {
        val admin = User(1, "admin", "123", "Admin IT","admin")
        val pegawai = User(2,"pegawai", "123", "Abdul Aziz", "pegawai")

        insertUser(admin)
        insertUser(pegawai)
    }
}
