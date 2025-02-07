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

/**
 * Pantalla de inicio de sesión donde los usuarios pueden ingresar sus credenciales
 * (correo electrónico y contraseña) para acceder a la aplicación.
 *
 * @param navController Controlador de navegación para redirigir entre pantallas.
 * @param onSignOut Función que se llama cuando el usuario cierra sesión.
 */
@Composable
fun SignInScreen(navController: NavController, onSignOut: () -> Unit) {

    // Instanciación de los manejadores de autenticación y Firestore
    val authManager = FirebaseAuthManager()  // Manejador de autenticación de Firebase para inicio de sesión
    val firestoreManager = FirebaseFirestoreManager()  // Manejador de Firestore para gestionar los datos del usuario

    // Variables de estado que almacenan los valores de los campos de correo y contraseña, y el mensaje de error
    val email = remember { mutableStateOf("") }  // Correo electrónico ingresado por el usuario
    val password = remember { mutableStateOf("") }  // Contraseña ingresada por el usuario
    val errorMessage = remember { mutableStateOf("") }  // Mensaje de error en caso de fallar el inicio de sesión

    // Creamos un scope de corrutinas para poder ejecutar tareas asíncronas y asi no bloqueamos el hilo principal
    val coroutineScope = rememberCoroutineScope()  // Manejador de corrutinas para realizar operaciones asíncronas

    // Definimos la pantalla con la estructura de Composables
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center  // Centra el contenido dentro del Box
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally  // Centra los elementos en la columna
            ) {
                // Campo para el correo electrónico
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },  // Actualiza el estado del correo con el texto ingresado
                    label = { Text("Correo electrónico") }  // Etiqueta para el campo
                )
                Spacer(modifier = Modifier.padding(8.dp))  // Espaciado entre los campos

                // Campo para la contraseña
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },  // Actualiza el estado de la contraseña
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation()  // Oculta la contraseña mientras se escribe
                )
                Spacer(modifier = Modifier.padding(8.dp))  // Espaciado entre los campos

                // Si existe un mensaje de error, se muestra
                if (errorMessage.value.isNotEmpty()) {
                    Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)  // Muestra el error con color rojo
                    Spacer(modifier = Modifier.padding(8.dp))
                }

                // Botón para iniciar sesión
                Button(onClick = {
                    // Llamada asíncrona para intentar iniciar sesión con las credenciales ingresadas
                    coroutineScope.launch {
                        val user = authManager.signInWithEmailAndPassword(email.value, password.value)
                        if (user != null) {  // Si el inicio de sesión fue exitoso
                            // Se actualiza el acceso del usuario en Firestore
                            val updated = firestoreManager.updateUserAccess(user.uid)

                            if (updated) {
                                // Si la actualización fue exitosa, se navega a la pantalla principal
                                navController.navigate("home")
                            } else {
                                // Si la actualización falla, se muestra un error
                                errorMessage.value = "Error al actualizar los datos del usuario."
                            }
                        } else {
                            // Si el inicio de sesión falla, se muestra un mensaje de error
                            errorMessage.value = "Error al iniciar sesión. Intenta nuevamente."
                        }
                    }
                }) {
                    Text("Iniciar sesión")  // Texto que aparece en el botón
                }

                Spacer(modifier = Modifier.height(16.dp))  // Espaciado entre los botones

                // Botón para navegar a la pantalla de registro
                Button(onClick = {
                    // Navega a la pantalla de registro
                    navController.navigate("register")
                }) {
                    Text("¿No tienes cuenta? Regístrate aquí")  // Texto que aparece en el botón de registro
                }
            }
        }
    }
}
