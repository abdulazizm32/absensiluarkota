package com.abdulazizmurtadho.uas.absensiluarkota

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LaporanActivity : AppCompatActivity() {
    private lateinit var rvLaporan: RecyclerView
    private lateinit var adapter: LaporanAdapter

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_laporan)
//
//        rvLaporan = findViewById(R.id.rv_laporan)
//        rvLaporan.layoutManager = LinearLayoutManager(this)
//        adapter = LaporanAdapter()
//        rvLaporan.adapter = adapter
//
//
//        loadData()
//        val btnRefresh = findViewById<Button>(R.id.btn_refresh)
//        btnRefresh.setOnClickListener { loadData() }
//        val tvJumlah = findViewById<TextView>(R.id.tv_jumlah)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        val firstApp = applicationContext as FirstApp  // ← FIX 1
        val absenDao = firstApp.getAbsenDao()

        lifecycleScope.launch {
            val list = absenDao.getAll()
            val resultText = buildString {
                append("${list.size} ABSEN DITEMUKAN\n\n")
                list.forEach { absen ->
                    append("ID: ${absen.id}\n")
                    append("Nama: ${absen.nama}\n")
                    append("Tanggal: ${absen.tanggal}\n")
                    append("Koordinat: ${absen.latitude}, ${absen.longitude}\n")
                    append("Foto: ${absen.fotoPath}\n")
                    append("---\n")
                }
            }

            val tvLaporan = findViewById<TextView>(R.id.tv_laporan)  // Tambah di XML
            tvLaporan.text = resultText
            Log.d("LaporanDebug", resultText)
        }
    }


    override fun onResume() {  // Auto refresh
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val firstApp = applicationContext as FirstApp  // ← FIX 1
        val absenDao = firstApp.getAbsenDao() // ← FIX 2\


        lifecycleScope.launch {
            try {
                val list = absenDao.getAll()  // Suspend getAll()

                // DEBUG LOG
                Log.d("LaporanDebug", "Total: ${list.size}")
                list.forEach { absen ->
                    Log.d("AbsenDebug", "ID:${absen.id} ${absen.nama} ${absen.tanggal}")
                }

                adapter.submitList(list)
            } catch (e: Exception) {
                Log.e("LaporanError", "Gagal load: ${e.message}")
            }
        }
    }
}
