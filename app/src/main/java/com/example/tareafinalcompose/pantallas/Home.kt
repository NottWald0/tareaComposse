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

/**
 * Pantalla principal que se muestra después de que el usuario haya iniciado sesión.
 * El propósito principal es mostrar la información del usuario y ofrecer opciones como cerrar sesión
 * o realizar una consulta.
 *
 * @param navController Controlador de navegación para redirigir entre pantallas.
 * @param onSignOut Función que se llama cuando el usuario cierra sesión.
 * @param onConsultationDone Función que se llama después de realizar una consulta.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    onSignOut: () -> Unit,
    onConsultationDone: () -> Unit
) {
    // Instancia del administrador de Firestore para acceder a los datos del usuario.
    val firestoreManager = FirebaseFirestoreManager()

    // Obtener el usuario actual desde Firestore (puede ser nulo si el usuario no está autenticado).
    val user = firestoreManager.getCurrentUser()

    // Estado que indica si el usuario ya ha realizado una consulta.
    var userHasConsulted by remember { mutableStateOf(false) }

    // Redirigir automáticamente al usuario a la pantalla de inicio de sesión si no está autenticado.
    // Esto solo se ejecuta cuando el usuario es nulo.
    LaunchedEffect(user?.uid) {
        if (user == null) {
            // Si no hay usuario, navegar a la pantalla de inicio de sesión y limpiar el historial de navegación.
            navController.navigate("signIn") {
                popUpTo("home") { inclusive = true } // Limpiar la pila de navegación para evitar regresar a la pantalla de inicio
            }
        }
    }

    // Layout principal de la pantalla con un Surface para establecer el fondo.
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        // Caja para centrar el contenido de la pantalla.
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            // Columna que organiza los elementos verticalmente en el centro de la pantalla.
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Mostrar un mensaje de bienvenida con el correo electrónico del usuario o "Usuario" si es nulo.
                Text("Bienvenido, ${user?.email ?: "Usuario"}", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(24.dp)) // Espacio entre los elementos de la interfaz

                // Botón para cerrar sesión
                Button(onClick = {
                    // Cerrar sesión en Firestore.
                    firestoreManager.signOut()
                    // Llamar a la función onSignOut para realizar acciones adicionales, como mostrar la pantalla de inicio.
                    onSignOut()
                    // Navegar a la pantalla de inicio de sesión y limpiar la pila de navegación.
                    navController.navigate("signIn") {
                        popUpTo("home") { inclusive = true }
                    }
                }) {
                    Text("Cerrar sesión") // Texto del botón para cerrar sesión
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los botones

                // Botón para realizar una consulta (redirige a la pantalla de consulta).
                Button(onClick = {
                    // Navegar a la pantalla de consulta (DogScreen).
                    navController.navigate("DogScreen")
                    // Cambiar el estado indicando que el usuario ha realizado la consulta.
                    userHasConsulted = true
                }) {
                    Text("Realizar consulta") // Texto del botón para realizar la consulta
                }
            }
        }
    }

    // Efecto para realizar acciones después de que el usuario haya regresado a la pantalla de inicio
    // Este LaunchedEffect se ejecuta solo cuando el estado del backstack cambia.
    LaunchedEffect(navController.currentBackStackEntry) {
        if (userHasConsulted) {
            // Si el usuario ha realizado la consulta, ejecuta la función onConsultationDone
            onConsultationDone()
        }
        // Resetear el estado de la consulta para evitar que la función se ejecute múltiples veces innecesarias.
        userHasConsulted = false
    }
}
