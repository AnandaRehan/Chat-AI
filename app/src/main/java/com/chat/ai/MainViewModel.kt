package com.chat.ai

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Masukkan API Key OpenRouter kamu di sini atau lewat UI
    var apiKey = mutableStateOf("sk-or-v1-YOUR_OPENROUTER_API_KEY_HERE")
    
    var availableModels = mutableStateListOf<String>()
    var selectedModel = mutableStateOf("google/gemini-2.0-flash-001") // Default
    
    var messages = mutableStateListOf<Message>()
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    init {
        fetchAvailableModels()
    }

    fun fetchAvailableModels() {
        viewModelScope.launch {
            try {
                val response = OpenRouterClient.service.getModels()
                val modelIds = response.data.map { it.id }.sorted()
                availableModels.clear()
                availableModels.addAll(modelIds)
                if (modelIds.isNotEmpty() && !modelIds.contains(selectedModel.value)) {
                    selectedModel.value = modelIds.first()
                }
            } catch (e: Exception) {
                // Jika gagal mengambil model dinamis, pakai fallback model
                availableModels.addAll(
                    listOf(
                        "google/gemini-2.0-flash-001",
                        "meta-llama/llama-3.3-70b-instruct",
                        "openai/gpt-4o-mini",
                        "deepseek/deepseek-r1"
                    )
                )
            }
        }
    }

    fun sendMessage(userPrompt: String) {
        if (userPrompt.isBlank() || apiKey.value.isBlank()) return

        val userMessage = Message(role = "user", content = userPrompt)
        messages.add(userMessage)
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val request = ChatRequest(
                    model = selectedModel.value,
                    messages = messages.toList()
                )
                
                val authHeader = "Bearer ${apiKey.value.trim()}"
                val response = OpenRouterClient.service.sendChat(
                    apiKey = authHeader,
                    request = request
                )

                val replyContent = response.choices?.firstOrNull()?.message?.content
                if (replyContent != null) {
                    messages.add(Message(role = "assistant", content = replyContent))
                } else {
                    errorMessage.value = "Respon kosong dari model."
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}