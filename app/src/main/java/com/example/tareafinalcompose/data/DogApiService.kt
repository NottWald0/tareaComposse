package com.example.tareafinalcompose.data

import com.example.tareafinalcompose.data.DogResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DogApiService {
    @GET("v1/images/search") // Endpoint para obtener una imagen aleatoria
    suspend fun getRandomDog(
        @Header("x-api-key") apiKey: String = "live_GYhiIHvoHYl4SLjmZVXMsK1kdt7igbM2XdQNp0oMYxHguX41eiuWhFRwEHZU8G53" // Aquí se pasa la API Key
    ): Response<List<DogResponse>>
}
