package com.abdulazizmurtadho.uas.absensiluarkota.screens
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.abdulazizmurtadho.uas.absensiluarkota.FirstApp
import com.abdulazizmurtadho.uas.absensiluarkota.viewmodel.LoginUiState
import com.abdulazizmurtadho.uas.absensiluarkota.viewmodel.LoginViewModel
import com.abdulazizmurtadho.uas.absensiluarkota.viewmodel.LoginViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val firstApp = context.applicationContext as FirstApp  // Fixed nama
    val absenDao = firstApp.getAbsenDao()

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(absenDao)
    )
    val loginState by viewModel.loginState.collectAsState()  // Fixed collectAsState

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()  // Keyboard safe
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFFAB63F1))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "Absensi Luar Kota",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(48.dp))

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username/NIP") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                // Error State
                if (loginState is LoginUiState.Error) {
                    Text(
                        text = (loginState as LoginUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        if (username.isBlank() || password.isBlank()) return@Button
                        viewModel.login(username, password, context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = loginState !is LoginUiState.Loading
                ) {
                    if (loginState is LoginUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("LOGIN", fontSize = 16.sp)
                    }
                }
            }
        }
    }

    // Navigasi Success
    LaunchedEffect(loginState) {
        if (loginState is LoginUiState.Success) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}