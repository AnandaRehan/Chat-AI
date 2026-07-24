package com.chat.ai

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chat.ai.data.ChatRequest
import com.chat.ai.data.FREE_AI_MODELS
import com.chat.ai.data.Message
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    // Simpan daftar percakapan
    val messages = mutableStateListOf<Message>()
    
    // Model AI yang dipilih (Default ke Gemini)
    val selectedModel = mutableStateOf(FREE_AI_MODELS[0])
    
    // Status apakah AI sedang mengetik/loading
    val isLoading = mutableStateOf(false)

    fun sendMessage(userText: String, apiKey: String) {
        if (userText.isBlank()) return

        // 1. Tambahkan pesan user ke layar
        val userMessage = Message(role = "user", content = userText)
        messages.add(userMessage)

        isLoading.value = true

        viewModelScope.launch {
            try {
                // 2. Siapkan data request
                val request = ChatRequest(
                    model = selectedModel.value.id,
                    messages = messages.toList() // Kirim konteks histori percakapan
                )

                // 3. Panggil API Retrofit (Ganti ApiClient sesuai instance retrofit kamu)
                val response = ApiClient.apiService.sendChat(
                    apiKey = "Bearer $apiKey",
                    request = request
                )

                // 4. Ambil balasan AI
                val botReply = response.choices?.firstOrNull()?.message?.content
                    ?: "Maaf, tidak ada respon dari AI."

                messages.add(Message(role = "assistant", content = botReply))

            } catch (e: Exception) {
                messages.add(Message(role = "assistant", content = "Error: ${e.localizedMessage}"))
            } finally {
                isLoading.value = false
            }
        }
    }
}
