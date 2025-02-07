package com.example.tareafinalcompose.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.tareafinalcompose.data.RetrofitInstance
import kotlinx.coroutines.launch

/**
 * Pantalla que muestra una imagen aleatoria de un perro obtenida desde una API externa.
 * La pantalla permite al usuario ver otro perro, regresar a la pantalla de inicio
 * o cerrar la aplicación.
 *
 * @param navController Controlador de navegación para redirigir a la pantalla de inicio.
 * @param onConsultationDone Función que se llama cuando se ha realizado la consulta
 *        para obtener la imagen del perro.
 */
@Composable
fun DogScreen(navController: NavController, onConsultationDone: () -> Unit) {
    // Crear un alcance de corrutinas para gestionar las tareas asíncronas
    val coroutineScope = rememberCoroutineScope()

    // Estado para almacenar la URL de la imagen del perro
    val dogImageUrl = remember { mutableStateOf("") }

    // Realizar la solicitud para obtener una imagen de perro cuando la pantalla se carga
    LaunchedEffect(true) {
        coroutineScope.launch {
            try {
                // Hacer la solicitud a la API para obtener un perro aleatorio
                val response = RetrofitInstance.dogApiService.getRandomDog()
                if (response.isSuccessful) {
                    // Si la respuesta es exitosa, asignar la URL de la imagen
                    dogImageUrl.value = response.body()?.firstOrNull()?.url ?: ""
                    // Marcar que la consulta fue realizada
                    onConsultationDone()
                }
            } catch (e: Exception) {
                // Manejar cualquier error (por ejemplo, error de red)
                dogImageUrl.value = ""
            }
        }
    }

    // Estructura principal de la pantalla con una columna
    Column(
        modifier = Modifier.fillMaxSize(), // Asegura que la columna ocupe toda la pantalla
        horizontalAlignment = Alignment.CenterHorizontally, // Centrar horizontalmente
        verticalArrangement = Arrangement.Center // Centrar verticalmente
    ) {
        // Mostrar la imagen del perro si la URL no está vacía
        if (dogImageUrl.value.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(dogImageUrl.value), // Cargar la imagen desde la URL
                contentDescription = "Random Dog", // Descripción de la imagen para accesibilidad
                modifier = Modifier.wrapContentSize(), // Ajustar el tamaño de la imagen
                contentScale = ContentScale.Crop // Recortar la imagen para ajustarla
            )
        } else {
            // Mostrar un texto mientras se carga la imagen
            Text("Cargando perro...")
        }

        // Botón para ver otro perro
        Button(onClick = {
            // Recargar una nueva imagen cuando el usuario hace clic
            coroutineScope.launch {
                dogImageUrl.value = "" // Limpiar la URL anterior
                val response = RetrofitInstance.dogApiService.getRandomDog()
                if (response.isSuccessful) {
                    // Asignar la nueva imagen si la solicitud fue exitosa
                    dogImageUrl.value = response.body()?.firstOrNull()?.url ?: ""
                }
            }
        }) {
            Text("Ver otro perro") // Texto del botón
        }

        // Botón para volver a la pantalla de inicio
        Button(onClick = {
            navController.navigate("home") // Navegar a la pantalla "Home"
        }) {
            Text("Volver") // Texto del botón
        }

        // Botón para cerrar la aplicación
        Button(onClick = {
            System.exit(0) // Cierra la aplicación
        }) {
            Text("Cerrar aplicación") // Texto del botón
        }
    }
}
