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

    val messages = mutableStateListOf<Message>()
    val selectedModel = mutableStateOf(FREE_AI_MODELS[0])
    val isLoading = mutableStateOf(false)

    fun sendMessage(userText: String, apiKey: String) {
        if (userText.isBlank()) return

        val userMessage = Message(role = "user", content = userText)
        messages.add(userMessage)

        isLoading.value = true

        viewModelScope.launch {
            try {
                val request = ChatRequest(
                    model = selectedModel.value.id,
                    messages = messages.toList()
                )

                val response = ApiClient.apiService.sendChat(
                    apiKey = "Bearer $apiKey",
                    request = request
                )

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
