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
fun HomeScreen(
    navController: NavController,
    onSignOut: () -> Unit,
    onConsultationDone: () -> Unit
) {
    // Instancia del administrador de Firestore
    val firestoreManager = FirebaseFirestoreManager()

    // Obtener el usuario actual desde Firestore
    val user = firestoreManager.getCurrentUser()

    // Estado que indica si el usuario ya ha realizado una consulta
    var userHasConsulted by remember { mutableStateOf(false) }

    // Si el usuario no está autenticado, redirigirlo a la pantalla de inicio de sesión
    LaunchedEffect(user?.uid) {
        if (user == null) {
            // Navegar a la pantalla de inicio de sesión
            navController.navigate("signIn") {
                popUpTo("home") { inclusive = true } // Limpiar la pila de navegación para no regresar a la pantalla de inicio
            }
        }
    }

    // Layout principal de la pantalla con un Surface para establecer el fondo
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        // Caja para centrar el contenido
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            // Columna que organiza los elementos verticalmente
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Mostrar el nombre o correo electrónico del usuario
                Text("Bienvenido, ${user?.email ?: "Usuario"}", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(24.dp)) // Espacio entre los elementos

                // Botón para cerrar sesión
                Button(onClick = {
                    // Cerrar sesión en Firestore
                    firestoreManager.signOut()
                    // Llamar a la función onSignOut para realizar acciones adicionales (ej. mostrar pantalla de inicio)
                    onSignOut()
                    // Navegar a la pantalla de inicio de sesión y limpiar la pila de navegación
                    navController.navigate("signIn") {
                        popUpTo("home") { inclusive = true }
                    }
                }) {
                    Text("Cerrar sesión") // Texto del botón
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los botones

                // Botón para realizar una consulta
                Button(onClick = {
                    // Redirigir a la pantalla de consulta (DogScreen)
                    navController.navigate("DogScreen")
                    // Cambiar el estado indicando que el usuario ha realizado la consulta
                    userHasConsulted = true
                }) {
                    Text("Realizar consulta") // Texto del botón
                }
            }
        }
    }

    // Efecto para realizar acciones después de que el usuario haya regresado a la pantalla de inicio
    LaunchedEffect(navController.currentBackStackEntry) {
        if (userHasConsulted) {
            // Ejecutar la función onConsultationDone para indicar que la consulta fue realizada
            onConsultationDone()
        }
        // Resetear el estado de la consulta
        userHasConsulted = false
    }
}
