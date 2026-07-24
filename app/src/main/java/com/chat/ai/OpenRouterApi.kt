package com.chat.ai

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// --- Data Model OpenRouter ---
data class ModelItem(val id: String, val name: String?)
data class ModelsResponse(val data: List<ModelItem>)

data class Message(val role: String, val content: String)
data class ChatRequest(val model: String, val messages: List<Message>)

data class ChatChoice(val message: Message)
data class ChatResponse(val choices: List<ChatChoice>?)

// --- Retrofit Interface ---
interface OpenRouterService {
    @GET("models")
    suspend fun getModels(): ModelsResponse

    @POST("chat/completions")
    suspend fun sendChat(
        @Header("Authorization") apiKey: String,
        @Header("HTTP-Referer") referer: String = "https://github.com/my-android-app",
        @Body request: ChatRequest
    ): ChatResponse
}

// --- Singleton Client ---
object OpenRouterClient {
    private const val BASE_URL = "https://openrouter.ai/api/v1/"

    val service: OpenRouterService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenRouterService::class.java)
    }
}