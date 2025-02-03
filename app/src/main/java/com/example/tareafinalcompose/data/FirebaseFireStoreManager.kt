import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreManager {
    private val db = FirebaseFirestore.getInstance()

    // Obtener el usuario actual
    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    // Agregar un nuevo usuario
    suspend fun addUser(userId: String, name: String, email: String) {
        val userData = mapOf(
            "name" to name,
            "email" to email,
            "accessCount" to 1,
            "lastAccessDate" to System.currentTimeMillis()
        )
        db.collection("users").document(userId).set(userData).await()
    }

    // Actualizar los datos del usuario (contador de accesos y fecha)
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

    // Obtener los datos de acceso del usuario
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

    // Cerrar sesión
    fun signOut() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            // Manejo de errores si es necesario
            e.printStackTrace()
        }
    }
}