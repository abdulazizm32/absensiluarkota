package com.abdulazizmurtadho.uas.absensiluarkota

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import java.io.File
import android.view.View

class MainActivity_backup : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvNamaUser: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnCekLokasi: Button
    private lateinit var btnAbsen: Button
    private lateinit var btnLaporan: Button

    private val lokasiKantor = Location("").apply {
        latitude = -8.0280654  // GANTI KOORDINAT UAS MALANG KAMU!
        longitude = 112.6329028
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) cekLokasi() else Toast.makeText(this, "GPS permission diperlukan!", Toast.LENGTH_SHORT).show()
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) ambilFoto() else Toast.makeText(this, "Kamera permission diperlukan!", Toast.LENGTH_SHORT).show()
    }
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        if (!prefs.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val namaUser = prefs.getString("nama", "Pegawai") ?: "Pegawai"

        val role = prefs.getString("role", "pegawai") ?: "pegawai"

        val btnSetKoordinat = findViewById<Button>(R.id.btnSetKoordinat)
        if (role == "admin") {
            btnSetKoordinat.visibility = View.VISIBLE
        } else {
            btnSetKoordinat.visibility = View.GONE
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            getSharedPreferences("user_session", MODE_PRIVATE).edit().clear().apply()

            Toast.makeText(this, "Logout sukses!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        db = AppDatabase.getDatabase(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tvNamaUser = findViewById<TextView>(R.id.tvNamaUser)
        tvNamaUser.text = namaUser
        tvStatus = findViewById(R.id.tvStatus)
        btnCekLokasi = findViewById(R.id.btnCekLokasi)
        btnAbsen = findViewById(R.id.btnAbsen)
        btnLaporan = findViewById(R.id.btnLaporan)
        btnAbsen.isEnabled = false  // Awal disable

        btnCekLokasi.setOnClickListener {
            if (cekGpsPermission()) cekLokasi()
        }

        btnAbsen.setOnClickListener {
            if (cekCameraPermission()) ambilFoto()
        }

        btnLaporan.setOnClickListener {
            val intent = Intent(this, LaporanActivity::class.java)
            startActivity(intent)
        }
        btnSetKoordinat.setOnClickListener {
            setKoordinatKantor()
        }
    }

    private fun cekGpsPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            false
        }
    }

    private fun cekCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            false
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun cekLokasi() {
        tvStatus.text = "Mencari GPS..."
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val distance = location.distanceTo(lokasiKantor)
                tvStatus.text = "Jarak kantor: ${String.format("%.0f", distance)} meter"
                if (distance < 100) {
                    tvStatus.text = "✅ DI KANTOR! (${String.format("%.0f", distance)}m) - Bisa absen!"
                    btnAbsen.isEnabled = true
                } else {
                    tvStatus.text = "❌ LUAR KANTOR! ${String.format("%.0f", distance)}m"
                    btnAbsen.isEnabled = false
                }
            } else {
                tvStatus.text = "GPS tidak tersedia"
            }
        }.addOnFailureListener {
            tvStatus.text = "Error GPS: ${it.message}"
        }
    }

    private fun ambilFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            CoroutineScope(Dispatchers.IO).launch {  // ← Coroutine DISINI!
                val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
                val nama = prefs.getString("nama", "Pegawai") ?: "Pegawai"
                if (nama.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity_backup, "Nama Harus Disi", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val tanggal = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

                val fotoPath = "${getExternalFilesDir(null)?.absolutePath}/foto_${System.currentTimeMillis()}.jpg"
                val bitmap = data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    try {
                        File(fotoPath).outputStream().use { out ->
                            it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        }
                    } catch (e: Exception) {
                        Log.e("error_log", "Gagal simpan foto: ${e.message}")
                    }
                }

                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val absen = Absen(
                                nama = nama,
                                tanggal = tanggal,
                                latitude = loc?.latitude ?: 0.0,
                                longitude = loc?.longitude ?: 0.0,
                                fotoPath = fotoPath
                            )
                            Log.d("log_db", "Insert absen: $absen")

                            db.absenDao().insert(absen)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity_backup,
                                    "✅ Absen $nama DISIMPAN!",
                                    Toast.LENGTH_LONG
                                ).show()
                                btnAbsen.isEnabled = false  // Reset
                            }
                        }catch (e: Exception) {
                                Log.e("error_log", "Insert error: ${e.message}", e)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@MainActivity_backup, "Gagal simpan: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity_backup, "Absen $nama DISIMPAN!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private val mapsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val lat = result.data?.getDoubleExtra("lat", 0.0) ?: 0.0
            val lng = result.data?.getDoubleExtra("lng", 0.0) ?: 0.0

        }
    }
    private fun setKoordinatKantor() {
        val intent = Intent(this, MapsActivity::class.java)
        mapsLauncher.launch(intent)
    }

}