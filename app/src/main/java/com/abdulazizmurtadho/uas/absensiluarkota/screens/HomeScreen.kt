package com.abdulazizmurtadho.uas.absensiluarkota.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.abdulazizmurtadho.uas.absensiluarkota.Absen
import com.abdulazizmurtadho.uas.absensiluarkota.AbsenDao
import com.abdulazizmurtadho.uas.absensiluarkota.AppDatabase
import com.abdulazizmurtadho.uas.absensiluarkota.FirstApp  // App class dengan DB
import com.abdulazizmurtadho.uas.absensiluarkota.LaporanActivity
import com.abdulazizmurtadho.uas.absensiluarkota.MapsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val nama = prefs.getString("nama", "User") ?: "User"
    val role = prefs.getString("role", "pegawai") ?: "pegawai"
    val isAdmin = role == "admin"
    Log.d("USERSDEBUG", "role: $role, nama: $nama")


    var statusText by remember { mutableStateOf("Klik Cek Lokasi") }
    var canAbsen by remember { mutableStateOf(false) }

    val firstApp = context.applicationContext as FirstApp
    val absenDao = firstApp.getAbsenDao()

    val prefs_lokasi = context.getSharedPreferences("appsettings", Context.MODE_PRIVATE)
    val lokasiKantor = remember {
        Location("").apply {
            latitude = prefs_lokasi.getFloat("kantorlat", -8.0280654f).toDouble()  // Load SharedPrefs
            longitude = prefs_lokasi.getFloat("kantorlng", 112.6329028f).toDouble()
        }
    }
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val gpsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cekLokasi(fusedLocationClient, lokasiKantor, context) { distance, can ->
                Log.e("lokasi", "Insert gagal: ${can}")
                statusText = if (can) "DI KANTOR! ${String.format("%.0f", distance)}m" else "LUAR KANTOR! ${String.format("%.0f", distance)}m"
                canAbsen = can
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            ambilFotoAbsen(result.data, nama, absenDao, fusedLocationClient, context)  // Enhanced function [file:1]
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selamat Datang",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = nama,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = if (isAdmin) "Role: Admin" else "Role: Pegawai",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(statusText, style = MaterialTheme.typography.bodyMedium)

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                            gpsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        } else {
                            cekLokasi(fusedLocationClient, lokasiKantor, context) { distance, can ->
                                statusText = if (can) "DI KANTOR! ${String.format("%.0f", distance)}m"
                                else "LUAR KANTOR! ${String.format("%.0f", distance)}m"
                                canAbsen = can
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CEK LOKASI")
                }

                val cameraPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                        Toast.makeText(context, "Camera siap!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Camera ditolak. Buka Settings > App > Permissions manual", Toast.LENGTH_LONG).show()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val cameraLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
                        ambilFotoAbsen(result.data, nama, absenDao, fusedLocationClient, context)
                    }
                }



                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canAbsen
                )   {
                    Text("Absen Sekarang")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        context.startActivity(Intent(context, LaporanActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LAPORAN")
                }

                if (isAdmin) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val intent = Intent(context, MapsActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SET LOKASI KANTOR")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        // Clear session SharedPreferences
                        context.getSharedPreferences("user_session", Context.MODE_PRIVATE).edit().clear().apply()
                        // Navigasi ke Login
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }  // Clear backstack
                        }
                        Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("LOGOUT", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

private fun cekLokasi(
    fusedLocationClient: FusedLocationProviderClient,
    lokasiKantor: Location,
    context: Context,
    onResult: (distance: Float, canAbsen: Boolean) -> Unit
) {
    var statusText = "Mencari GPS..."

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        statusText = "Gagal Akses permission GPS!"
        return
    }

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                val distance = location.distanceTo(lokasiKantor)
                val canAbsen = distance <= 100f
                onResult(distance, canAbsen)
                Log.d("GPS_OK", "Distance: $distance m")
            } else {
                statusText = "GPS belum tersedia, aktifkan location services"
                Toast.makeText(context, "Buka GPS/Location services", Toast.LENGTH_LONG).show()
            }
        }
        .addOnFailureListener {
            statusText = "Error GPS"
            Log.e("GPS_FAIL", it.message ?: "Unknown")
            Toast.makeText(context, "GPS error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}

private fun ambilFotoAbsen(data: Intent?, nama: String, absenDao: AbsenDao, fusedLocationClient: FusedLocationProviderClient, context: Context ) {
    val bitmap = data?.extras?.get("data") as? Bitmap
    bitmap ?: run {
        Toast.makeText(context, "Foto gagal diambil", Toast.LENGTH_SHORT).show()
        return
    }

    val fotoPath = "${context.getExternalFilesDir(null)?.absolutePath}/foto_${System.currentTimeMillis()}.jpg"
    try {
        File(fotoPath).outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tanggal = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    val absen = Absen(
                        nama = nama,
                        tanggal = tanggal,
                        latitude = loc?.latitude ?: 0.0,
                        longitude = loc?.longitude ?: 0.0,
                        fotoPath = fotoPath
                    )
//                    absenDao.insert(absen)
                    val insertedId = absenDao.insert(absen)
                    Log.d("AbsenDebug", "Sukses! Inserted ID: $insertedId")

                    try {
//                        val absenDao = db.getAbsenDao()
                        val list = absenDao.getAll()

                        Log.d("LaporanDebug", "Total absen: ${list.size}")

                        list.forEachIndexed { index, absen ->
                            Log.d("AbsenDebug",
                                "[$index] ID:${absen.id} | ${absen.nama} | ${absen.tanggal} | " +
                                        "Lat:${absen.latitude} Lng:${absen.longitude} | Foto:${absen.fotoPath}"
                            )
                        }

//                        adapter.submitList(list)
//                        tvJumlah.text = "${list.size} absen ditemukan"

                    } catch (e: Exception) {
                        Log.e("LaporanError", "getAll gagal: ${e.message}")
                    }

//                        absenDao.getAll()

                    withContext(Dispatchers.Main) {
                        Log.d("Absen-tess", "Inserted: $absen")
                        Toast.makeText(context, "Absen $nama DISIMPAN!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("AbsenError", "Insert gagal: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Gagal simpan: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    } catch (e: Exception) {
        Log.e("FotoError", "Simpan foto gagal: ${e.message}")
        Toast.makeText(context, "Gagal simpan foto", Toast.LENGTH_SHORT).show()
    }
}

