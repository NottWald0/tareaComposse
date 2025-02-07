package com.example.tareafinalcompose.pantallas

import FirebaseFirestoreManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareafinalcompose.data.FirebaseAuthManager
import kotlinx.coroutines.launch

/**
 * Pantalla de registro donde los usuarios pueden crear una cuenta proporcionando su
 * nombre, correo electrónico, contraseña y confirmación de contraseña.
 *
 * La pantalla también valida los datos de entrada y realiza el registro del usuario
 * en Firebase Authentication y guarda los datos del usuario en Firestore.
 *
 * @param navController Controlador de navegación para redirigir al usuario a la pantalla principal
 *        después de un registro exitoso.
 */
@Composable
fun RegisterScreen(navController: NavController) {

    // Instancia de los manejadores de autenticación y Firestore
    val authManager = FirebaseAuthManager()  // Se encarga de la autenticación de Firebase
    val firestoreManager = FirebaseFirestoreManager()  // Se encarga de la gestión de Firestore

    // Variables de estado para capturar la entrada del usuario
    val email = remember { mutableStateOf("") }  // Email proporcionado por el usuario
    val password = remember { mutableStateOf("") }  // Contraseña proporcionada por el usuario
    val confirmPassword = remember { mutableStateOf("") }  // Confirmación de la contraseña
    val errorMessage = remember { mutableStateOf("") }  // Mensaje de error si la validación falla
    val isUserRegistered = remember { mutableStateOf(false) }  // Estado para verificar si el usuario fue registrado correctamente
    val name = remember { mutableStateOf("") }  // Nombre proporcionado por el usuario

    // Alcance de corutina para realizar operaciones asincrónicas (registro de usuario)
    val coroutineScope = rememberCoroutineScope()

    // Redirige a la pantalla de inicio después de un registro exitoso
    LaunchedEffect(isUserRegistered.value) {
        if (isUserRegistered.value) {
            navController.navigate("home")  // Redirige a la pantalla principal
        }
    }

    /**
     * Función que maneja el proceso de registro del usuario.
     * Verifica que todos los campos estén completos y que las contraseñas coincidan,
     * luego realiza el registro a través de Firebase Authentication y guarda los datos
     * del usuario en Firestore.
     */
    fun registerUser() {
        // Verifica que todos los campos estén completos
        if (email.value.isEmpty() || password.value.isEmpty() || confirmPassword.value.isEmpty() || name.value.isEmpty()) {
            errorMessage.value = "Todos los campos son obligatorios"  // Mensaje de error si falta algún campo
            return
        }

        // Verifica que las contraseñas coincidan
        if (password.value != confirmPassword.value) {
            errorMessage.value = "Las contraseñas no coinciden"  // Mensaje de error si las contraseñas no coinciden
            return
        }

        // Inicia el proceso de registro asincrónico
        coroutineScope.launch {
            val userId = authManager.registerUser(email.value, password.value)  // Registra al usuario en Firebase
            if (userId != null) {
                // Si el registro es exitoso, guarda los datos en Firestore
                firestoreManager.addUser(userId, name.value, email.value)
                isUserRegistered.value = true  // Indica que el usuario fue registrado exitosamente
            } else {
                errorMessage.value = "Error al registrar el usuario. Intenta nuevamente."  // Mensaje de error si ocurre algún problema
            }
        }
    }

    // Diseño de la pantalla de registro
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Título de la pantalla
                Text(text = "Registrarse", style = MaterialTheme.typography.headlineSmall)

                // Campo de entrada para el nombre del usuario
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },  // Actualiza el valor del nombre
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.value.isEmpty()  // Muestra un error si el campo está vacío
                )

                // Campo de entrada para el correo electrónico
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },  // Actualiza el valor del correo
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = email.value.isEmpty()  // Muestra un error si el campo está vacío
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de entrada para la contraseña
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },  // Actualiza el valor de la contraseña
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = password.value.isEmpty(),  // Muestra un error si el campo está vacío
                    visualTransformation = PasswordVisualTransformation()  // Enmascara la contraseña
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de entrada para confirmar la contraseña
                TextField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },  // Actualiza el valor de la confirmación de la contraseña
                    label = { Text("Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPassword.value.isEmpty(),  // Muestra un error si el campo está vacío
                    visualTransformation = PasswordVisualTransformation()  // Enmascara la confirmación de la contraseña
                )

                // Muestra el mensaje de error si ocurre algún problema con la entrada del usuario
                if (errorMessage.value.isNotEmpty()) {
                    Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)  // Muestra el error en rojo
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Botón para registrar al usuario
                Button(
                    onClick = { registerUser() },  // Ejecuta el proceso de registro
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.value.isNotEmpty() && password.value.isNotEmpty() && confirmPassword.value.isNotEmpty() && name.value.isNotEmpty()  // Habilita el botón solo si todos los campos están completos
                ) {
                    Text("Registrar")  // Texto del botón
                }
            }
        }
    }
}

/**
 * Vista previa de la pantalla de registro en el entorno de desarrollo.
 * Esta función permite ver cómo se renderiza la pantalla sin necesidad de ejecutarla en un dispositivo.
 */
@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    val navController = rememberNavController()  // Se crea un controlador de navegación para la vista previa
    RegisterScreen(navController = navController)  // Muestra la pantalla de registro en la vista previa
}
