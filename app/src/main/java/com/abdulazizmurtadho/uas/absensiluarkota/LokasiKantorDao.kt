package com.abdulazizmurtadho.uas.absensiluarkota
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface LokasiKantorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lokasi: LokasiKantor)

    @Query("SELECT * FROM lokasi_kantor LIMIT 1")
    suspend fun getLokasiKantor(): LokasiKantor?
}