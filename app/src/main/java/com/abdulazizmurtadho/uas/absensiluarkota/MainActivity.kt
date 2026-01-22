package com.abdulazizmurtadho.uas.absensiluarkota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abdulazizmurtadho.uas.absensiluarkota.screens.AdminScreen
import com.abdulazizmurtadho.uas.absensiluarkota.screens.HomeScreen
import com.abdulazizmurtadho.uas.absensiluarkota.screens.LoginScreen
import com.abdulazizmurtadho.uas.absensiluarkota.screens.SplashScreen
import com.abdulazizmurtadho.uas.absensiluarkota.ui.theme.AbsensiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var db: AppDatabase
        db = AppDatabase.getDatabase(this)
        super.onCreate(savedInstanceState)
        setContent {
            AbsensiTheme {  // WAJIB wrapper ini!
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AbsensiNavHost()
                }
            }
        }
    }
}


@Composable
fun AbsensiNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
//        composable("laporan") { LaporanScreen() }
        composable("admin") { AdminScreen(navController) }
    }
}