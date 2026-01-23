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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        val firstApp = applicationContext as FirstApp
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


    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val firstApp = applicationContext as FirstApp
        val absenDao = firstApp.getAbsenDao()


        lifecycleScope.launch {
            try {
                val list = absenDao.getAll()

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
