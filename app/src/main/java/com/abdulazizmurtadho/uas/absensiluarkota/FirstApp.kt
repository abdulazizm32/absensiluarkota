package com.abdulazizmurtadho.uas.absensiluarkota
import android.app.Application
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirstApp : Application() {  // Ganti nama class
    lateinit var database: AppDatabase

    companion object {
        @Volatile
        private var INSTANCE: FirstApp? = null  // Ganti type

        fun getInstance(): FirstApp {  // Ganti return type
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirstApp().also { INSTANCE = it }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "absensi_database"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            database.absenDao().ensureTestUsers()
            Log.d("DB_INSERT", "Test users inserted!")
        }
    }

    fun getAbsenDao() = database.absenDao()
//    fun getLokasiKantorDao() = database.lokasiKantorDao()


}