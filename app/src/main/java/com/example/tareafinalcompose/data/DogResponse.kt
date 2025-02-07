package com.example.tareafinalcompose.data

/**
 * Modelo de datos que representa la respuesta de la API The Dog API.
 * Contiene la URL de la imagen del perro obtenida al hacer una solicitud.
 *
 * La API devuelve una lista de objetos con más información, pero en este caso
 * solo se necesita la URL de la imagen.
 *
 * @property url Dirección web de la imagen del perro.
 */
data class DogResponse(
    val url: String // URL de la imagen del perro proporcionada por la API
)
