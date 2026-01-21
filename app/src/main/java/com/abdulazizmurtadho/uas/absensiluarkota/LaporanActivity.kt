package com.abdulazizmurtadho.uas.absensiluarkota

import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager


import androidx.room.Room

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LaporanActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var rvLaporan: RecyclerView
    private lateinit var adapter: LaporanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        db = AppDatabase.getDatabase(this)

        rvLaporan = findViewById(R.id.rvLaporan)
        rvLaporan.layoutManager = LinearLayoutManager(this)
        adapter = LaporanAdapter()
        rvLaporan.adapter = adapter

        // ambil data dari Room
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.absenDao().getAll()
            withContext(Dispatchers.Main) {
                adapter.submitList(list)
            }
        }
    }
}