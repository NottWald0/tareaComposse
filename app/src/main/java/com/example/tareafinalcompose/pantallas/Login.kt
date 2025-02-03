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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareafinalcompose.data.FirebaseAuthManager
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navController: NavController) {
    val authManager = FirebaseAuthManager()
    val firestoreManager = FirebaseFirestoreManager()

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

    // Coroutines scope para lanzar tareas dentro de un Composable
    val coroutineScope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Correo electrónico") })
                Spacer(modifier = Modifier.padding(8.dp))
                TextField(value = password.value, onValueChange = { password.value = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation())
                Spacer(modifier = Modifier.padding(8.dp))

                // Mostrar mensaje de error si existe
                if (errorMessage.value.isNotEmpty()) {
                    Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.padding(8.dp))
                }

                Button(onClick = {
                    coroutineScope.launch {
                        // Intentamos iniciar sesión
                        val user = authManager.signInWithEmailAndPassword(email.value, password.value)
                        if (user != null) {
                            // Si el inicio de sesión es exitoso, incrementamos el contador de accesos y actualizamos la fecha
                            val updated = firestoreManager.updateUserAccess(user.uid)

                            if (updated) {
                                // Navegar a la pantalla principal
                                navController.navigate("home")
                            } else {
                                // Si no se pudo actualizar, mostramos un error
                                errorMessage.value = "Error al actualizar los datos del usuario."
                            }
                        } else {
                            // Si el inicio de sesión falla, mostramos un error
                            errorMessage.value = "Error al iniciar sesión. Intenta nuevamente."
                        }
                    }
                }) {
                    Text("Iniciar sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    // Navegar a la pantalla de registro si el usuario no tiene cuenta
                    navController.navigate("register")
                }) {
                    Text("¿No tienes cuenta? Regístrate aquí")
                }
            }
        }
    }
}
