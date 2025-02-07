package com.example.tareafinalcompose.data

import FirebaseFirestoreManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * FirebaseAuthManager se encarga de gestionar la autenticación de usuarios
 * en la aplicación utilizando Firebase Authentication.
 * Proporciona métodos para iniciar sesión, registrarse, cerrar sesión y
 * verificar el estado del usuario.
 */
class FirebaseAuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Verifica si hay un usuario autenticado en la sesión actual.
     * @return true si hay un usuario autenticado, false en caso contrario.
     */
    fun isUserLoggedIn(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    /**
     * Inicia sesión con email y contraseña utilizando Firebase Authentication.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return El usuario autenticado (FirebaseUser) si el inicio de sesión es exitoso,
     *         null si ocurre un error.
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return try {
            val result: AuthResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            // Si el usuario existe, se actualiza su acceso en Firestore
            user?.uid?.let {
                FirebaseFirestoreManager().updateUserAccess(it)
            }

            user
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Registra un nuevo usuario en Firebase Authentication con email y contraseña.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return UID del usuario registrado si el registro es exitoso, null si falla.
     */
    suspend fun registerUser(email: String, password: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (e: Exception) {
            println("Error al registrar el usuario: ${e.message}")
            null
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Obtiene el usuario actualmente autenticado en la sesión.
     * @return El usuario autenticado (FirebaseUser) si existe, null en caso contrario.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
