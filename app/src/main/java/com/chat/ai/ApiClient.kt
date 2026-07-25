package com.chat.ai

import com.chat.ai.data.ChatRequest
import com.chat.ai.data.ChatResponse
import com.chat.ai.data.GeminiRequest
import com.chat.ai.data.GeminiResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    // OpenRouter API
    @POST("v1/chat/completions")
    suspend fun sendOpenRouterChat(
        @Header("Authorization") apiKey: String,
        @Header("HTTP-Referer") referer: String = "https://github.com/AnandaRehan/Chat-AI",
        @Body request: ChatRequest
    ): ChatResponse

    // Google Gemini Direct API
    @POST
    suspend fun sendGeminiChat(
        @Url url: String,
        @Body request: GeminiRequest
    ): GeminiResponse
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
