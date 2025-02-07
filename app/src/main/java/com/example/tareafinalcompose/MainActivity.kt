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

class MainActivity : ComponentActivity() {

    // Variable para verificar si el usuario realizó la consulta a la API
    private var userHasConsulted by mutableStateOf(false)

    // Handler para ejecutar el Runnable periódicamente
    private val handler = Handler(Looper.getMainLooper())

    // Variable para almacenar la pantalla actual
    private var currentScreen by mutableStateOf("home")

    // Runnable que se encargará de mostrar las notificaciones periódicas
    private val toastRunnable = object : Runnable {
        override fun run() {
            // Mostrar la notificación solo si el usuario no ha consultado la API y si no está en la pantalla de inicio de sesión
            if (!userHasConsulted && currentScreen != "signin") {
                showNotification()

                // Volver a ejecutar el Runnable cada 500 ms
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val authManager = FirebaseAuthManager()

            // Determinar la pantalla de inicio según el estado de autenticación del usuario
            val startDestination = if (authManager.isUserLoggedIn()) "home" else "signin"

            // Configuración de la navegación
            NavHost(navController = navController, startDestination = startDestination) {
                composable("signin") {
                    currentScreen = "signin"
                    SignInScreen(navController, onSignOut = { onSignOut(navController) })
                }

                composable("home") {
                    currentScreen = "home"
                    userHasConsulted = false

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

                // Ruta para la pantalla de registro
                composable("register") {
                    RegisterScreen(navController)
                }
            }
        }

        // Mostrar una notificación al iniciar la aplicación
        showNotification()
    }

    /**
     * Función para mostrar una notificación al usuario
     */
    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel"

        // Crear el canal de notificación para Android 8.0 y superior
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorio de consulta",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Construcción de la notificación
        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Recuerda consultar la API")
            .setContentText("¡No olvides consultar la API para más información!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }

    /**
     * Función que se ejecuta cuando el usuario ha realizado la consulta
     */
    private fun onConsultationDone() {
        userHasConsulted = true
        handler.removeCallbacks(toastRunnable) // Detener las notificaciones periódicas
    }

    /**
     * Función que maneja el cierre de sesión
     */
    private fun onSignOut(navController: NavController) {
        handler.removeCallbacks(toastRunnable) // Detener el Runnable al cerrar sesión
        userHasConsulted = false

        // Redirigir a la pantalla de inicio de sesión y limpiar la pila de navegación
        navController.navigate("signin") {
            popUpTo("signin") { inclusive = true }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(toastRunnable) // Asegurar que el Runnable se detiene al destruir la actividad
    }
}