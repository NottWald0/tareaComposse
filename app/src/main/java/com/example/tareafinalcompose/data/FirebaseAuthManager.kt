package com.example.tareafinalcompose.data

import FirebaseFirestoreManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Verificar si el usuario está autenticado
    fun isUserLoggedIn(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    // Iniciar sesión con email y contraseña
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return try {
            val result: AuthResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            // Si el usuario existe, actualizar el acceso
            user?.uid?.let {
                FirebaseFirestoreManager().updateUserAccess(it)  // Llamamos a la función para actualizar el acceso
            }

            user
        } catch (e: Exception) {
            null
        }
    }

    // Registrar usuario
    suspend fun registerUser(email: String, password: String): String? {
        return try {
            // Crear el usuario
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (e: Exception) {
            println("Error al registrar el usuario: ${e.message}")
            null
        }
    }

    // Cerrar sesión
    fun signOut() {
        auth.signOut()
    }

    // Obtener usuario actual
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
