package com.abdulazizmurtadho.uas.absensiluarkota

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val password: String,
    val nama: String,
    val role: String  // "admin" atau "pegawai"
)