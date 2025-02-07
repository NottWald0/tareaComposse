package com.example.tareafinalcompose.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * Interfaz que define el servicio de Retrofit para interactuar con The Dog API.
 * Proporciona un método para obtener una imagen aleatoria de un perro desde la API.
 */
interface DogApiService {

    /**
     * Realiza una solicitud GET al endpoint "v1/images/search" para obtener una imagen aleatoria de un perro.
     *
     * @param apiKey Clave de autenticación para la API (se pasa como encabezado HTTP).
     * @return Una lista de objetos [DogResponse] envuelta en un [Response] de Retrofit.
     */
    @GET("v1/images/search") // Endpoint de la API para obtener una imagen de un perro aleatorio
    suspend fun getRandomDog(
        @Header("x-api-key") apiKey: String = "live_GYhiIHvoHYl4SLjmZVXMsK1kdt7igbM2XdQNp0oMYxHguX41eiuWhFRwEHZU8G53" // API Key requerida para la autenticación
    ): Response<List<DogResponse>>
}
