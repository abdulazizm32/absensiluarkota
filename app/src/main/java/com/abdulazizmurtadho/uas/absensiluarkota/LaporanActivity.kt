package com.abdulazizmurtadho.uas.absensiluarkota
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class LaporanActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(this)
        loadAndShowLaporan()
    }

    private fun loadAndShowLaporan() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = db.absenDao()
                val list = dao?.getAll() ?: emptyList()

                withContext(Dispatchers.Main) {
                    val items = list.map {
                        "${it.nama} - ${it.tanggal}\nLat:${it.latitude} Lng:${it.longitude}"
                    }

                    AlertDialog.Builder(this@LaporanActivity)
                        .setTitle("Daftar Absen (${list.size})")
                        .setItems(items.toTypedArray()) { _, _ -> }
                        .setPositiveButton("OK") { _, _ -> finish() }
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LaporanActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}