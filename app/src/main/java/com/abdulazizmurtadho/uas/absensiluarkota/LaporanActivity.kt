package com.abdulazizmurtadho.uas.absensiluarkota

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdulazizmurtadho.uas.absensiluarkota.screens.LaporanScreen
import com.abdulazizmurtadho.uas.absensiluarkota.ui.theme.AbsensiTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//class LaporanActivity : AppCompatActivity() {
//    private lateinit var rvLaporan: RecyclerView
//    private lateinit var adapter: LaporanAdapter
//    private lateinit var db: FirstApp
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_laporan)
//
//        rvLaporan = findViewById(R.id.rv_laporan)
//        rvLaporan.layoutManager = LinearLayoutManager(this)
//        adapter = LaporanAdapter()
//        rvLaporan.adapter = adapter
//
//        loadData()
//
////        findViewById<FloatingActionButton>(R.id.)?.setOnClickListener {
////            loadData()
////        }
//    }
//
//    private fun loadData() {
//        lifecycleScope.launch {
//            val list = db.getAbsenDao().getAll()
//            adapter.updateData(list)
//        }
//    }
//}
class LaporanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AbsensiTheme {  // Theme Anda
                LaporanScreen()
            }
        }
    }
}

