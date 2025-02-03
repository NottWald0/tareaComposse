package com.example.tareafinalcompose.pantallas

import FirebaseFirestoreManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    val firestoreManager = FirebaseFirestoreManager()
    val user = firestoreManager.getCurrentUser() // Obtener usuario actual

    var accessCount by remember { mutableStateOf(0L) }
    var lastAccessDate by remember { mutableStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()

    // Obtener datos de acceso del usuario actual y actualizar los accesos
    LaunchedEffect(user?.uid) {
        user?.uid?.let {
            // Si el usuario ya está registrado, actualizamos el contador de accesos
            val data = firestoreManager.getUserAccessData(it)
            if (data != null) {
                accessCount = data.first
                lastAccessDate = data.second
            }

            val updateSuccess = firestoreManager.updateUserAccess(it)
            if (updateSuccess) {
                val updatedData = firestoreManager.getUserAccessData(it)
                if (updatedData != null) {
                    accessCount = updatedData.first
                    lastAccessDate = updatedData.second
                }
            }
        } ?: run {
            // Si el usuario no está autenticado, redirigimos a la pantalla de registro
            navController.navigate("register")
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text("Bienvenido, ${user?.email ?: "Usuario"}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Número de accesos: $accessCount", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))

                if (lastAccessDate > 0) {
                    Text("Último acceso: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(java.util.Date(lastAccessDate))}")
                } else {
                    Text("Nunca has accedido antes.")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = {
                    // Cerrar sesión y redirigir a la pantalla de inicio de sesión
                    firestoreManager.signOut()
                    navController.navigate("signIn")
                }) {
                    Text("Cerrar sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    // Mostrar el botón para hacer una consulta cuando el usuario esté autenticado
                    navController.navigate("consult")
                }) {
                    Text("Realizar consulta")
                }
            }
        }
    }
}
