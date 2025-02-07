package com.example.tareafinalcompose.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto Singleton que gestiona la instancia de Retrofit para realizar peticiones a la API de The Dog API.
 * Se encarga de configurar Retrofit con la URL base y el convertidor JSON para la serialización/deserialización.
 */
object RetrofitInstance {
    private const val BASE_URL = "https://api.thedogapi.com/" // URL base de la API

    /**
     * Servicio de la API de perros, inicializado de manera perezosa (lazy).
     * Esto significa que solo se creará la instancia cuando se acceda por primera vez a 'dogApiService'.
     */
    val dogApiService: DogApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Define la URL base de la API
            .addConverterFactory(GsonConverterFactory.create()) // Agrega el convertidor de JSON a objetos Kotlin
            .build()
            .create(DogApiService::class.java) // Crea la implementación de la interfaz de servicio
    }
}
