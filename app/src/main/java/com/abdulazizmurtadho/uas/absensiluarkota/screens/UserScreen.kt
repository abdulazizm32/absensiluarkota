package com.abdulazizmurtadho.uas.absensiluarkota.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.abdulazizmurtadho.uas.absensiluarkota.FirstApp
import com.abdulazizmurtadho.uas.absensiluarkota.User
import com.abdulazizmurtadho.uas.absensiluarkota.viewmodel.UserViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.collectAsState



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController) {
    val context = LocalContext.current
    val firstApp = context.applicationContext as FirstApp
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory(firstApp))

    val users by userViewModel.users.collectAsState()
    var nama by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var editingUser by remember { mutableStateOf<User?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Form Tambah/Edit
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(if (editingUser == null) "Tambah Pegawai" else "Edit Pegawai", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Role: pegawai (Fixed)", style = MaterialTheme.typography.bodyMedium)

                Row {
                    Button(onClick = {
                        val user = User(
                            id = editingUser?.id ?: 0,
                            nama = nama,
                            username = username,
                            password = password,
                            role = "pegawai"
                        )
                        userViewModel.setCurrentUser(user)
                        if (editingUser == null) userViewModel.saveUser() else userViewModel.updateUser()

                        // Reset form
                        nama = ""; username = ""; password = ""; editingUser = null
                    }) {
                        Text(if (editingUser == null) "Tambah" else "Update")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { nama = ""; username = ""; password = ""; editingUser = null }) {
                        Text("Batal")
                    }
                }
            }
        }

        // List Users
        LazyColumn {
            items(users) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${user.nama} (${user.username})", style = MaterialTheme.typography.bodyLarge)
                            Text("Role: ${user.role}", style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = {
                            editingUser = user
                            nama = user.nama
                            username = user.username
                            password = user.password
                        }) {
                            Icon(Icons.Default.Edit, "Edit")
                        }
                        IconButton(onClick = { userViewModel.deleteUser(user) }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    }
                }
            }
        }
    }
}
