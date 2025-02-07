import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * FirebaseFirestoreManager gestiona la interacción con Firestore para almacenar y actualizar
 * los datos de los usuarios. Permite agregar nuevos usuarios, actualizar sus accesos,
 * recuperar información de acceso y cerrar sesión.
 */
class FirebaseFirestoreManager {
    private val db = FirebaseFirestore.getInstance()

    /**
     * Obtiene el usuario actualmente autenticado en Firebase Authentication.
     * @return FirebaseUser si hay un usuario autenticado, null en caso contrario.
     */
    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    /**
     * Agrega un nuevo usuario a la colección "users" en Firestore.
     * @param userId UID del usuario en Firebase Authentication.
     * @param name Nombre del usuario.
     * @param email Correo electrónico del usuario.
     */
    suspend fun addUser(userId: String, name: String, email: String) {
        val userData = mapOf(
            "name" to name,
            "email" to email,
            "accessCount" to 1, // Inicializa el contador de accesos en 1
            "lastAccessDate" to System.currentTimeMillis() // Guarda la fecha actual como último acceso
        )
        db.collection("users").document(userId).set(userData).await()
    }

    /**
     * Actualiza el contador de accesos y la fecha del último acceso del usuario en Firestore.
     * @param userId UID del usuario en Firebase Authentication.
     * @return true si la actualización es exitosa, false si el usuario no existe o hay un error.
     */
    suspend fun updateUserAccess(userId: String): Boolean {
        return try {
            val userRef = db.collection("users").document(userId)
            val userSnapshot = userRef.get().await()
            if (userSnapshot.exists()) {
                val currentAccessCount = userSnapshot.getLong("accessCount") ?: 0
                val newAccessCount = currentAccessCount + 1
                val currentDate = Timestamp.now()
                userRef.update(
                    "accessCount", newAccessCount,
                    "lastAccessDate", currentDate
                ).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtiene los datos de acceso del usuario, incluyendo el número de accesos
     * y la fecha del último acceso.
     * @param userId UID del usuario en Firebase Authentication.
     * @return Un par (accessCount, lastAccessDate) si el usuario existe, null en caso contrario.
     */
    suspend fun getUserAccessData(userId: String): Pair<Long, Long>? {
        val userRef = db.collection("users").document(userId)
        val userSnapshot = userRef.get().await()
        if (userSnapshot.exists()) {
            val accessCount = userSnapshot.getLong("accessCount") ?: 0
            val lastAccessDate = userSnapshot.getLong("lastAccessDate") ?: 0
            return Pair(accessCount, lastAccessDate)
        }
        return null
    }

    /**
     * Cierra la sesión del usuario actual en Firebase Authentication.
     * Maneja posibles errores durante el cierre de sesión.
     */
    fun signOut() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            // Manejo de errores si es necesario
            e.printStackTrace()
        }
    }
}
