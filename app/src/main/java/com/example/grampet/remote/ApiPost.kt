package com.example.grampet.remote

import com.google.gson.annotations.SerializedName

/**
 * Este es el modelo de datos que coincide con la respuesta de la API de Picsum.
 */
data class ApiPost(
    @SerializedName("id")
    val id: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("download_url")
    val downloadUrl: String
)
