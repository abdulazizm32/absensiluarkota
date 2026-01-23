package com.abdulazizmurtadho.uas.absensiluarkota
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface LokasiKantorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lokasi: LokasiKantor):Long

    @Query("SELECT * FROM lokasi_kantor ORDER BY id DESC LIMIT 1")
    suspend fun getLokasiKantor(): LokasiKantor?


}