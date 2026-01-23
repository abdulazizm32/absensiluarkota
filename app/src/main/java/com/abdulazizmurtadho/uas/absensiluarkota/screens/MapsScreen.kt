package com.abdulazizmurtadho.uas.absensiluarkota.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.abdulazizmurtadho.uas.absensiluarkota.FirstApp
import com.abdulazizmurtadho.uas.absensiluarkota.LokasiKantor
import com.mapbox.maps.plugin.gestures.gestures
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedPoint by remember { mutableStateOf<Point?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Lokasi Kantor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Mapbox View
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        getMapboxMap().loadStyle(Style.MAPBOX_STREETS)
                        getMapboxMap().setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(112.6329, -8.0281))
                                .zoom(13.0)
                                .build()
                        )

                        // Klik peta
                        gestures.addOnMapClickListener { point ->
                            selectedPoint = point
                            true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // FAB Zoom
            FloatingActionButton(
                onClick = { /* zoom logic */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.ZoomIn, "Zoom")
            }
        }

        // Dialog konfirmasi
        selectedPoint?.let { point ->
            ConfirmLocationDialog(point) { confirmed ->
                if (confirmed) {
                    coroutineScope.launch {
                        saveToDatabase(context, point)
                    }
                    navController.popBackStack()
                }
                selectedPoint = null
            }
        }
    }
}

@Composable
private fun ConfirmLocationDialog(point: Point, onDismiss: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = { Text("Konfirmasi Lokasi") },
        text = {
            Text(
                text = buildString {
                    append("Latitude: ${String.format("%.6f", point.latitude())}\n")
                    append("Longitude: ${String.format("%.6f", point.longitude())}")
                }
            )
        },
        confirmButton = {
            TextButton(onClick = { onDismiss(true) }) {
                Text("SIMPAN KE DATABASE")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss(false) }) {
                Text("PILIH LAGI")
            }
        }
    )
}

suspend fun saveToDatabase(context: Context, point: Point) {
    try {
        val firstApp = context.applicationContext as FirstApp
        val lokasiDao = firstApp.getLokasiDao()

        val lokasi = LokasiKantor(
            latitude = point.latitude(),
            longitude = point.longitude()
        )

        val id = lokasiDao.insert(lokasi)
        Log.d("MapsScreen", "Lokasi tersimpan! ID: $id")

        // Main thread toast
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(context, "Lokasi kantor tersimpan di DB! ID: $id", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Log.e("MapsError", "Save failed: ${e.message}")
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(context, "Gagal simpan: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
