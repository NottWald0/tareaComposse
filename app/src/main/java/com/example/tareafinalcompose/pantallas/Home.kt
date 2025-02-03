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

    val coroutineScope = rememberCoroutineScope()

    // Si el usuario no está autenticado, redirigir a la pantalla de registro
    LaunchedEffect(user?.uid) {
        if (user == null) {
            navController.navigate("signIn")
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text("Bienvenido, ${user?.email ?: "Usuario"}", style = MaterialTheme.typography.titleLarge)
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
                    // Redirigir a la pantalla de consulta
                    navController.navigate("consult")
                }) {
                    Text("Realizar consulta")
                }
            }
        }
    }
}
