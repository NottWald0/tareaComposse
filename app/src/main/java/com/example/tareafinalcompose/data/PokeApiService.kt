package com.example.tareafinalcompose.data

import retrofit2.http.GET
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemons(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): PokeApiResponse
}
