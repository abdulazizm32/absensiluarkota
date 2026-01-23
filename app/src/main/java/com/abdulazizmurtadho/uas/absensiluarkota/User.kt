package com.abdulazizmurtadho.uas.absensiluarkota

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama: String = "",
    val username: String = "",
    val password: String= "",
    val role: String= "pegawai"
)
