package com.chat.ai

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chat.ai.data.AiModel
import com.chat.ai.data.ChatRequest
import com.chat.ai.data.ChatSession
import com.chat.ai.data.FREE_AI_MODELS
import com.chat.ai.data.GeminiContent
import com.chat.ai.data.GeminiPart
import com.chat.ai.data.GeminiRequest
import com.chat.ai.data.Message
import com.chat.ai.data.Provider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

class ChatViewModel : ViewModel() {

    val sessions = mutableStateListOf<ChatSession>()
    val currentSessionId = mutableStateOf<String?>(null)

    val selectedModel = mutableStateOf(FREE_AI_MODELS[0])
    val isLoading = mutableStateOf(false)
    
    val openRouterApiKey = mutableStateOf("")
    val geminiApiKey = mutableStateOf("")

    val showSettingsDialog = mutableStateOf(false)
    val settingsErrorMessage = mutableStateOf<String?>(null)

    val themeMode = mutableStateOf(ThemeMode.SYSTEM)

    // Channel untuk antrean request (FIFO Queue)
    private val requestChannel = Channel<ChatSession>(Channel.UNLIMITED)

    init {
        createNewChat()
        startQueueProcessor()
    }

    private fun startQueueProcessor() {
        viewModelScope.launch {
            for (session in requestChannel) {
                processAiRequest(session)
            }
        }
    }

    fun createNewChat() {
        val newSession = ChatSession()
        sessions.add(0, newSession)
        currentSessionId.value = newSession.id
    }

    fun selectSession(id: String) {
        currentSessionId.value = id
    }

    fun deleteSession(id: String) {
        sessions.removeAll { it.id == id }
        if (currentSessionId.value == id) {
            if (sessions.isNotEmpty()) {
                currentSessionId.value = sessions[0].id
            } else {
                createNewChat()
            }
        }
    }

    fun getCurrentSession(): ChatSession? {
        return sessions.find { it.id == currentSessionId.value }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val activeModel = selectedModel.value

        if (activeModel.provider == Provider.OPEN_ROUTER && openRouterApiKey.value.isBlank()) {
            settingsErrorMessage.value = "⚠️ OpenRouter API Key belum diisi!"
            showSettingsDialog.value = true
            return
        }

        if (activeModel.provider == Provider.GEMINI && geminiApiKey.value.isBlank()) {
            settingsErrorMessage.value = "⚠️ Google Gemini API Key belum diisi!"
            showSettingsDialog.value = true
            return
        }

        val currentSession = getCurrentSession() ?: return

        if (currentSession.messages.isEmpty()) {
            currentSession.title = if (userText.length > 22) userText.take(22) + "..." else userText
        }

        val userMessage = Message(role = "user", content = userText)
        currentSession.messages.add(userMessage)

        requestChannel.trySend(currentSession)
    }

    private suspend fun processAiRequest(session: ChatSession) {
        isLoading.value = true
        val activeModel = selectedModel.value

        try {
            var rawBotReply = ""

            if (activeModel.provider == Provider.OPEN_ROUTER) {
                val request = ChatRequest(
                    model = activeModel.id,
                    messages = session.messages.toList()
                )

                val response = ApiClient.apiService.sendOpenRouterChat(
                    apiKey = "Bearer ${openRouterApiKey.value}",
                    request = request
                )

                rawBotReply = response.choices?.firstOrNull()?.message?.content
                    ?: "Maaf, tidak ada respon dari OpenRouter."

            } else if (activeModel.provider == Provider.GEMINI) {
                val geminiContents = session.messages.map { msg ->
                    GeminiContent(
                        role = if (msg.role == "assistant") "model" else "user",
                        parts = listOf(GeminiPart(text = msg.content))
                    )
                }

                val request = GeminiRequest(contents = geminiContents)
                val geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/${activeModel.id}:generateContent?key=${geminiApiKey.value}"

                val response = ApiClient.apiService.sendGeminiChat(
                    url = geminiUrl,
                    request = request
                )

                rawBotReply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Maaf, tidak ada respon dari Gemini API."
            }

            isLoading.value = false

            simulateStreamingResponse(session, rawBotReply)

        } catch (e: Exception) {
            isLoading.value = false
            session.messages.add(Message(role = "assistant", content = "Error: ${e.localizedMessage}"))
        }
    }

    private suspend fun simulateStreamingResponse(session: ChatSession, fullText: String) {
        val botMessage = Message(role = "assistant", content = "")
        session.messages.add(botMessage)
        val lastIndex = session.messages.size - 1

        val words = fullText.split(" ")
        var currentContent = ""
        var i = 0

        while (i < words.size) {
            val chunkSize = Random.nextInt(1, 5)
            val end = minOf(i + chunkSize, words.size)

            val chunk = words.subList(i, end).joinToString(" ")
            currentContent = if (currentContent.isEmpty()) chunk else "$currentContent $chunk"

            session.messages[lastIndex] = botMessage.copy(content = currentContent)

            val randomDelay = Random.nextLong(40, 160)
            delay(randomDelay)

            i = end
        }
    }
}
