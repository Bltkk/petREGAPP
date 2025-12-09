package com.example.grampet.model

// Esta es la clase de modelo de datos principal para una publicación.
data class Post(
    val id: Int,
    val username: String,
    // El modelo puede ser cualquier cosa:
    // - Un Int para un recurso drawable (R.drawable.img_1)
    // - Un String para una URL de internet
    // - Un Bitmap para una foto tomada con la cámara
    val model: Any,
    val description: String
)
