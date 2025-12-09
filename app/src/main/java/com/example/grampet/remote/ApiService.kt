package com.example.grampet.remote

import com.example.grampet.remote.model.LoginRequest
import com.example.grampet.remote.model.LoginResponse
import com.example.grampet.remote.model.SignUpRequest
import com.example.grampet.remote.model.SignUpResponse
import com.example.grampet.remote.model.PostResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse

    @GET("posts")
    suspend fun getPosts(): List<PostResponse>
}