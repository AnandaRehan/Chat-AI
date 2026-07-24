package com.chat.ai

import com.chat.ai.data.ChatRequest
import com.chat.ai.data.ChatResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("v1/chat/completions")
    suspend fun sendChat(
        @Header("Authorization") apiKey: String,
        @Header("HTTP-Referer") referer: String = "https://github.com/AnandaRehan/Chat-AI", // Ganti dengan link github kamu
        @Body request: ChatRequest
    ): ChatResponse
}

object ApiClient {
    private const val BASE_URL = "https://openrouter.ai/api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
