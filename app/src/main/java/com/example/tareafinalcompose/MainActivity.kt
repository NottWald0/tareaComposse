package com.example.tareafinalcompose

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tareafinalcompose.data.FirebaseAuthManager
import com.example.tareafinalcompose.pantallas.DogScreen
import com.example.tareafinalcompose.pantallas.HomeScreen
import com.example.tareafinalcompose.pantallas.RegisterScreen // Importa RegisterScreen
import com.example.tareafinalcompose.pantallas.SignInScreen

/**
 * Actividad principal de la aplicación. Gestiona la autenticación de usuario, la navegación
 * entre las pantallas y la presentación de notificaciones periódicas si el usuario no ha realizado
 * una consulta a la API.
 *
 * @param navController Controlador de navegación utilizado para redirigir entre las pantallas.
 */
class MainActivity : ComponentActivity() {

    // Variable que indica si el usuario ha realizado la consulta a la API
    private var userHasConsulted by mutableStateOf(false)

    // Handler que maneja la ejecución periódica de tareas en el hilo principal
    private val handler = Handler(Looper.getMainLooper())

    // Variable que guarda el nombre de la pantalla actual
    private var currentScreen by mutableStateOf("home")

    // Runnable que se ejecuta periódicamente para mostrar notificaciones si el usuario no ha consultado la API
    private val toastRunnable = object : Runnable {
        override fun run() {
            // Solo muestra la notificación si el usuario no ha realizado la consulta y no está en la pantalla de inicio de sesión
            if (!userHasConsulted && currentScreen != "signin") {
                showNotification() // Mostrar la notificación de recordatorio

                // Reejecutar el Runnable cada 500 ms
                handler.postDelayed(this, 500)
            }
        }
    }

    /**
     * Función que se llama cuando la actividad es creada. Inicializa la pantalla de inicio
     * y configura la navegación, mostrando una notificación al usuario.
     *
     * @param savedInstanceState Estado guardado de la actividad si está presente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Activar diseño de pantalla completa sin bordes

        setContent {
            val navController = rememberNavController() // Controlador de navegación
            val authManager = FirebaseAuthManager() // Gestor de autenticación con Firebase

            // Determinar la pantalla inicial según si el usuario está autenticado o no
            val startDestination = if (authManager.isUserLoggedIn()) "home" else "signin"

            // Configuración de la navegación entre las diferentes pantallas
            NavHost(navController = navController, startDestination = startDestination) {
                composable("signin") {
                    currentScreen = "signin" // Actualizar pantalla actual a inicio de sesión
                    SignInScreen(navController, onSignOut = { onSignOut(navController) })
                }

                composable("home") {
                    currentScreen = "home" // Actualizar pantalla actual a inicio
                    userHasConsulted = false // Restablecer estado de consulta

                    // Pantalla principal de la aplicación
                    HomeScreen(
                        navController,
                        onSignOut = { onSignOut(navController) },
                        onConsultationDone = { onConsultationDone() }
                    )

                    // Iniciar el Runnable solo si el usuario no ha consultado
                    if (!userHasConsulted) {
                        handler.post(toastRunnable)
                    }
                }

                composable("DogScreen") {
                    DogScreen(navController, onConsultationDone = { onConsultationDone() })
                }

                // Pantalla para el registro del usuario
                composable("register") {
                    RegisterScreen(navController)
                }
            }
        }

        // Mostrar notificación al iniciar la actividad
        showNotification()
    }

    /**
     * Muestra una notificación al usuario para recordarle realizar la consulta a la API.
     */
    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel" // Canal de notificación

        // Crear el canal de notificación si el dispositivo es Android 8.0 o superior
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorio de consulta", // Nombre del canal
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel) // Crear canal
        }

        // Crear la notificación con título y mensaje
        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Recuerda consultar la API")
            .setContentText("¡No olvides consultar la API para más información!")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono de la notificación
            .build()

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }

    /**
     * Función que se llama cuando el usuario ha realizado la consulta a la API.
     * Detiene las notificaciones periódicas.
     */
    private fun onConsultationDone() {
        userHasConsulted = true
        handler.removeCallbacks(toastRunnable) // Detener las notificaciones periódicas
    }

    /**
     * Función que maneja el cierre de sesión del usuario, deteniendo el Runnable
     * y redirigiendo a la pantalla de inicio de sesión.
     *
     * @param navController Controlador de navegación para redirigir a la pantalla de inicio de sesión.
     */
    private fun onSignOut(navController: NavController) {
        handler.removeCallbacks(toastRunnable) // Detener el Runnable al cerrar sesión
        userHasConsulted = false

        // Redirigir a la pantalla de inicio de sesión y limpiar la pila de navegación
        navController.navigate("signin") {
            popUpTo("signin") { inclusive = true }
        }
    }

    /**
     * Función que se llama cuando la actividad es destruida. Detiene el Runnable
     * para evitar que continúe ejecutándose después de que la actividad se cierre.
     */
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(toastRunnable) // Asegurarse de que el Runnable se detiene
    }
}
