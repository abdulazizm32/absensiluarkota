package com.abdulazizmurtadho.uas.absensiluarkota

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import androidx.compose.ui.geometry.Offset  // Hanya ini
import com.mapbox.maps.plugin.gestures.gestures

class MapsActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mapView = findViewById(R.id.mapView)
        mapboxMap = mapView.mapboxMap

        mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
            // v11: listener kasih POINT langsung! No convert needed
            mapView.gestures.addOnMapClickListener { point: Point ->
                Toast.makeText(this, "Klik: ${point.latitude()}, ${point.longitude()}", Toast.LENGTH_LONG).show()
                showConfirmDialog(point)
                true  // Consume click
            }

            // Malang center
            mapboxMap.setCamera(CameraOptions.Builder()
                .center(Point.fromLngLat(112.6329, -8.0281))
                .zoom(13.0)
                .build())
        }
    }

    private fun showConfirmDialog(point: Point) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Lokasi Kantor")
            .setMessage("Lat: ${"%.6f".format(point.latitude())}\nLng: ${"%.6f".format(point.longitude())}")
            .setPositiveButton("Simpan") { _, _ ->
                getSharedPreferences("app_settings", MODE_PRIVATE).edit().apply {
                    putFloat("kantor_lat", point.latitude().toFloat())
                    putFloat("kantor_lng", point.longitude().toFloat())
                    apply()
                }
                setResult(RESULT_OK, Intent().apply {
                    putExtra("lat", point.latitude())
                    putExtra("lng", point.longitude())
                })
                finish()
            }
            .setNegativeButton("Ulangi", null)
            .show()
    }

    // Lifecycle
    override fun onStart() { super.onStart(); mapView.onStart() }
    override fun onStop() { super.onStop(); mapView.onStop() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
}