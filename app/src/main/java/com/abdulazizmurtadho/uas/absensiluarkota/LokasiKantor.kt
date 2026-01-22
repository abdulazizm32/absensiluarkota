package com.abdulazizmurtadho.uas.absensiluarkota

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lokasi_kantor")
data class LokasiKantor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double
)
