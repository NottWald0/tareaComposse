package com.example.tareafinalcompose.pantallas

import FirebaseFirestoreManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController, onSignOut: () -> Unit, onConsultationDone: () -> Unit) {
    val firestoreManager = FirebaseFirestoreManager()
    val user = firestoreManager.getCurrentUser() // Obtener usuario actual

    var userHasConsulted by remember { mutableStateOf(false) } // Estado para controlar si el usuario ya consultó

    // Si el usuario no está autenticado, redirigir a la pantalla de registro
    LaunchedEffect(user?.uid) {
        if (user == null) {
            navController.navigate("signIn") {
                popUpTo("home") { inclusive = true } // Limpiar la pila de navegación para que no se pueda volver a la pantalla de inicio
            }
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
                    onSignOut() // Ejecuta la función onSignOut pasada como parámetro
                    navController.navigate("signIn") {
                        popUpTo("home") { inclusive = true } // Limpiar la pila de navegación para que no se pueda volver a la pantalla de inicio
                    }
                }) {
                    Text("Cerrar sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    // Redirigir a la pantalla de consulta (DogScreen)
                    navController.navigate("DogScreen")
                    userHasConsulted = true // Cambiar el estado cuando se hace la consulta
                }) {
                    Text("Realizar consulta")
                }
            }
        }
    }

    // Resetear el estado de consulta al regresar a Home
    LaunchedEffect(navController.currentBackStackEntry) {
        if (userHasConsulted) {
            onConsultationDone()  // Marca que la consulta fue realizada
        }
        userHasConsulted = false // Resetear el estado después de la consulta
    }
}