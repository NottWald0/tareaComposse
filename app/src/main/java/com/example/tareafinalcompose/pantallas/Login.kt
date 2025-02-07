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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tareafinalcompose.data.FirebaseAuthManager
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navController: NavController, onSignOut: () -> Unit) {
    val authManager = FirebaseAuthManager()
    val firestoreManager = FirebaseFirestoreManager()

    // Variables de estado para el email, contraseña y mensaje de error
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

    // Scope de corrutinas para manejar tareas asincrónicas
    val coroutineScope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Campo de entrada para el correo electrónico
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Correo electrónico") }
                )
                Spacer(modifier = Modifier.padding(8.dp))

                // Campo de entrada para la contraseña
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.padding(8.dp))

                // Mostrar mensaje de error si existe
                if (errorMessage.value.isNotEmpty()) {
                    Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.padding(8.dp))
                }

                // Botón para iniciar sesión
                Button(onClick = {
                    coroutineScope.launch {
                        // Intentamos iniciar sesión con FirebaseAuthManager
                        val user = authManager.signInWithEmailAndPassword(email.value, password.value)
                        if (user != null) {
                            // Si el inicio de sesión es exitoso, actualizamos datos en Firestore
                            val updated = firestoreManager.updateUserAccess(user.uid)

                            if (updated) {
                                // Navegar a la pantalla principal si todo sale bien
                                navController.navigate("home")
                            } else {
                                // Mostrar mensaje de error si falla la actualización
                                errorMessage.value = "Error al actualizar los datos del usuario."
                            }
                        } else {
                            // Mostrar mensaje de error si el inicio de sesión falla
                            errorMessage.value = "Error al iniciar sesión. Intenta nuevamente."
                        }
                    }
                }) {
                    Text("Iniciar sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para ir a la pantalla de registro
                Button(onClick = {
                    navController.navigate("register")
                }) {
                    Text("¿No tienes cuenta? Regístrate aquí")
                }
            }
        }
    }
}
