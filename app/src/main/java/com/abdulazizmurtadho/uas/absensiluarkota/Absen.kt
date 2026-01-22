package com.abdulazizmurtadho.uas.absensiluarkota

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "absensi")
data class Absen(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val tanggal: String,
    val latitude: Double,
    val longitude: Double,
    val fotoPath: String
)