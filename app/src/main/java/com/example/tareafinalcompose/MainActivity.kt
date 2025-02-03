package com.example.tareafinalcompose


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tareafinalcompose.data.FirebaseAuthManager
import com.example.tareafinalcompose.pantallas.DogScreen
import com.example.tareafinalcompose.pantallas.HomeScreen

import com.example.tareafinalcompose.pantallas.RegisterScreen
import com.example.tareafinalcompose.pantallas.SignInScreen
import com.example.tareafinalcompose.ui.theme.TareaFinalComposeTheme

class MainActivity : ComponentActivity() {
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
                // Pantalla de inicio de sesión
                composable("signin") {
                    SignInScreen(navController)
                }

                // Pantalla de registro
                composable("register") {
                    RegisterScreen(navController)
                }

                // Pantalla principal (Home)
                composable("home") {
                    HomeScreen(navController)
                }
                composable("dogScreen") {
                    DogScreen(navController)  // Pantalla donde se muestra la imagen del perro
                }




                }
            }
        }
    }


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TareaFinalComposeTheme {
        Greeting("Android")
    }
}
