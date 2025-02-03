package com.example.tareafinalcompose.pantallas

import androidx.compose.foundation.Image
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.tareafinalcompose.data.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun DogScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val dogImageUrl = remember { mutableStateOf("") }

    // Hacer la solicitud cuando la pantalla se carga
    LaunchedEffect(true) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.dogApiService.getRandomDog()
                if (response.isSuccessful) {
                    dogImageUrl.value = response.body()?.firstOrNull()?.url ?: ""
                }
            } catch (e: Exception) {
                dogImageUrl.value = ""
                // Manejo de error
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        // Mostrar imagen del perro si hay una URL
        if (dogImageUrl.value.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(dogImageUrl.value),
                contentDescription = "Random Dog",
                modifier = Modifier.wrapContentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text("Cargando perro...")
        }

        Button(onClick = {
            // Recargar imagen al hacer clic
            coroutineScope.launch {
                dogImageUrl.value = ""
                val response = RetrofitInstance.dogApiService.getRandomDog()
                if (response.isSuccessful) {
                    dogImageUrl.value = response.body()?.firstOrNull()?.url ?: ""
                }
            }
        }) {
            Text("Ver otro perro")
        }

        // Botón para volver a la pantalla Home
        Button(onClick = {
            navController.navigate("home") // Navegar a la pantalla "Home"
        }) {
            Text("Volver")
        }
    }
}
