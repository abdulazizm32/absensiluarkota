package com.abdulazizmurtadho.uas.absensiluarkota

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Absen::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun absenDao(): AbsenDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "absen_db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO users (username,password,nama,role) VALUES ('admin','123','Admin IT','admin')")
                            db.execSQL("INSERT INTO users (username,password,nama,role) VALUES ('pegawai','123','Abdul Aziz','pegawai')")
                        }
                    })
                    .fallbackToDestructiveMigration()  // ← TAMBAH INI
                    .build().also { INSTANCE = it }
            }
        }
    }
}