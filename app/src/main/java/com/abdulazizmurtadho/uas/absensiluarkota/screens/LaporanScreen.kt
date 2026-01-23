package com.abdulazizmurtadho.uas.absensiluarkota.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.abdulazizmurtadho.uas.absensiluarkota.Absen  // ← Pastikan path benar
import com.abdulazizmurtadho.uas.absensiluarkota.FirstApp  // ← Pastikan path benar
import com.abdulazizmurtadho.uas.absensiluarkota.ui.theme.AbsensiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen() {
    val context = LocalContext.current
    var listAbsen by remember { mutableStateOf(listOf<Absen>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        loadAbsensi(context) { absens ->
            listAbsen = absens
            isLoading = false
        }
    }

    AbsensiTheme {  // ← Ganti dengan theme Anda
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Laporan Absensi") }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isLoading) {
                    item { LoadingCard() }
                } else if (listAbsen.isEmpty()) {
                    item { EmptyState() }
                } else {
                    items(listAbsen) { absen ->
                        AbsenCard(absen)
                    }
                }
            }
        }
    }
}

@Composable
fun AbsenCard(absen: Absen) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = absen.fotoPath,
                contentDescription = "Foto absen",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = android.R.drawable.ic_menu_gallery)  // Fallback
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = absen.nama ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = absen.tanggal,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Lat:${absen.latitude} Lng:${absen.longitude}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

suspend fun loadAbsensi(context: Context, callback: (List<Absen>) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            val firstApp = context.applicationContext as FirstApp
            val absenDao = firstApp.getAbsenDao()
            val list = absenDao.getAll()
            callback(list)
        } catch (e: Exception) {
            Log.e("Laporanabvsensi", "Load error: ${e.message}")
        }
    }
}

@Composable
fun LoadingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(64.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.List, "No data", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Belum ada data absensi\nAbsen dulu yuk!")
        }
    }
}

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
