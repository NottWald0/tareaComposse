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

    private var userHasConsulted by mutableStateOf(false) // Variable para verificar si el usuario realizó la consulta
    private val handler = Handler(Looper.getMainLooper()) // Handler para ejecutar el Runnable
    private var currentScreen by mutableStateOf("home") // Variable para almacenar la pantalla actual

    // Runnable que se encargará de mostrar las notificaciones periódicas
    private val toastRunnable = object : Runnable {
        override fun run() {
            // Mostrar la notificación solo si el usuario no ha consultado la API y si no está en la pantalla de inicio de sesión
            if (!userHasConsulted && currentScreen != "signin") {
                // Mostrar la notificación periódica
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

            // Comprobamos si el usuario está autenticado al iniciar la app
            val startDestination = if (authManager.isUserLoggedIn()) "home" else "signin"

            // Configuramos el NavHost con la pantalla correspondiente como destino inicial
            NavHost(navController = navController, startDestination = startDestination) {
                composable("signin") {
                    // Actualizamos el estado actual a "signin"
                    currentScreen = "signin"
                    SignInScreen(navController, onSignOut = { onSignOut(navController) })
                }

                composable("home") {
                    // Restablecer la variable cuando el usuario regrese a la pantalla de inicio
                    currentScreen = "home"
                    userHasConsulted = false

                    // Mostrar la notificación solo si el usuario está en la pantalla de inicio
                    HomeScreen(navController, onSignOut = { onSignOut(navController) }, onConsultationDone = { onConsultationDone() })

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
                    RegisterScreen(navController) // Navegar a RegisterScreen
                }
            }
        }

        // Mostrar una notificación cuando el usuario acceda a la aplicación
        showNotification()
    }

    // Función para mostrar una notificación
    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel"

        // Crear el canal de notificación (para Android 8.0 y versiones superiores)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorio de consulta",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Construir la notificación
        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Recuerda consultar la API")
            .setContentText("¡No olvides consultar la API para más información!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }

    // Función que marcará al usuario como "ya consultado"
    private fun onConsultationDone() {
        // Cambiar el estado a que el usuario ha hecho la consulta
        userHasConsulted = true

        // Detener el Runnable cuando el usuario realiza la consulta
        handler.removeCallbacks(toastRunnable) // Detenemos el Runnable de las notificaciones
    }

    // Función que manejará el cierre de sesión
    private fun onSignOut(navController: NavController) {
        // Detener el Runnable al cerrar sesión
        handler.removeCallbacks(toastRunnable)

        // Restablecer el estado de la consulta y otras variables
        userHasConsulted = false

        // Redirigir a la pantalla de inicio de sesión
        navController.navigate("signin") {
            // Limpiar la pila de navegación para que no se pueda volver a la pantalla de home después de cerrar sesión
            popUpTo("signin") { inclusive = true }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(toastRunnable) // Asegurarse de detener el Runnable si la actividad se destruye
    }
}
