package com.abdulazizmurtadho.uas.absensiluarkota.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.plugin.gestures.gestures

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedPoint by remember { mutableStateOf<Point?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Lokasi Kantor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        getMapboxMap().loadStyle(Style.MAPBOX_STREETS) {
                            getMapboxMap().setCamera(
                                CameraOptions.Builder()
                                    .center(Point.fromLngLat(112.6329, -8.0281))
                                    .zoom(13.0)
                                    .build()
                            )
                        }

                        // Click handler
                        gestures.addOnMapClickListener { point ->
                            selectedPoint = point
                            true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Dialog jika point dipilih
            selectedPoint?.let { point ->
                ConfirmDialog(point) { confirmed ->
                    if (confirmed) {
                        saveLocation(context, point)
                        navController.popBackStack()
                    }
                    selectedPoint = null
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(point: Point, onDismiss: (Boolean) -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = { Text("Konfirmasi Lokasi") },
        text = {
            Text(
                "Simpan lokasi kantor?\n" +
                        "Lat: ${"%.6f".format(point.latitude())}\n" +
                        "Lng: ${"%.6f".format(point.longitude())}"
            )
        },
        confirmButton = {
            TextButton(onClick = { onDismiss(true) }) {
                Text("SIMPAN")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss(false) }) {
                Text("ULANGI")
            }
        }
    )
}

fun saveLocation(context: Context, point: Point) {
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    prefs.edit()
        .putFloat("kantor_lat", point.latitude().toFloat())
        .putFloat("kantor_lng", point.longitude().toFloat())
        .apply()

    Toast.makeText(context, "Lokasi kantor tersimpan!", Toast.LENGTH_LONG)
}
