package com.example.tareafinalcompose.pantallas

import FirebaseFirestoreManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun RegisterScreen(navController: NavController) {

    // Instancia de los manejadores de autenticación y firestore
    val authManager = FirebaseAuthManager()
    val firestoreManager = FirebaseFirestoreManager()

    // Estados para almacenar los valores ingresados por el usuario
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val isUserRegistered = remember { mutableStateOf(false) }
    val name = remember { mutableStateOf("") }

    // Crear un alcance de corutina para ejecutar operaciones asíncronas
    val coroutineScope = rememberCoroutineScope()

    // Navegar a la pantalla de inicio después de que el usuario esté registrado
    LaunchedEffect(isUserRegistered.value) {
        if (isUserRegistered.value) {
            navController.navigate("home")
        }
    }

    // Función para registrar al usuario
    fun registerUser() {
        // Verificar que todos los campos estén llenos
        if (email.value.isEmpty() || password.value.isEmpty() || confirmPassword.value.isEmpty() || name.value.isEmpty()) {
            errorMessage.value = "Todos los campos son obligatorios"
            return
        }

        // Verificar que las contraseñas coincidan
        if (password.value != confirmPassword.value) {
            errorMessage.value = "Las contraseñas no coinciden"
            return
        }

        // Intentar registrar al usuario de manera asíncrona
        coroutineScope.launch {
            val userId = authManager.registerUser(email.value, password.value)
            if (userId != null) {
                firestoreManager.addUser(userId, name.value, email.value)
                isUserRegistered.value = true
            } else {
                errorMessage.value = "Error al registrar el usuario. Intenta nuevamente."
            }
        }
    }

    // Estructura de la pantalla de registro
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Título de la pantalla
                Text(text = "Registrarse", style = MaterialTheme.typography.headlineSmall)

                // Campo para el nombre del usuario
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.value.isEmpty()
                )

                // Campo para el correo electrónico
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = email.value.isEmpty()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo para la contraseña
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = password.value.isEmpty(),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo para confirmar la contraseña
                TextField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    label = { Text("Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPassword.value.isEmpty(),
                    visualTransformation = PasswordVisualTransformation()
                )

                // Mostrar mensaje de error si existe
                if (errorMessage.value.isNotEmpty()) {
                    Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Botón de registro
                Button(
                    onClick = { registerUser() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.value.isNotEmpty() && password.value.isNotEmpty() && confirmPassword.value.isNotEmpty() && name.value.isNotEmpty()
                ) {
                    Text("Registrar")
                }
            }
        }
    }
}

// Vista previa para mostrar la pantalla en el entorno de desarrollo
@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    val navController = rememberNavController()
    RegisterScreen(navController = navController)
}
