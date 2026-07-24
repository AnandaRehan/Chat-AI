package com.chat.ai

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chat.ai.data.ChatRequest
import com.chat.ai.data.ChatSession
import com.chat.ai.data.FREE_AI_MODELS
import com.chat.ai.data.Message
import kotlinx.coroutines.launch

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

class ChatViewModel : ViewModel() {

    // Daftar semua sesi obrolan
    val sessions = mutableStateListOf<ChatSession>()
    val currentSessionId = mutableStateOf<String?>(null)

    val selectedModel = mutableStateOf(FREE_AI_MODELS[0])
    val isLoading = mutableStateOf(false)
    
    // API Key (Default kosong agar pengguna wajib mengisi)
    val apiKey = mutableStateOf("")

    // State untuk kontrol Dialog Settings dari mana saja
    val showSettingsDialog = mutableStateOf(false)
    val settingsErrorMessage = mutableStateOf<String?>(null)

    // State untuk Pengaturan Tema
    val themeMode = mutableStateOf(ThemeMode.SYSTEM)

    init {
        createNewChat()
    }

    // Buat Obrolan Baru
    fun createNewChat() {
        val newSession = ChatSession()
        sessions.add(0, newSession)
        currentSessionId.value = newSession.id
    }

    // Pilih Obrolan dari History
    fun selectSession(id: String) {
        currentSessionId.value = id
    }

    // Hapus Obrolan dari History
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

    // Ambil Sesi Obrolan Saat Ini
    fun getCurrentSession(): ChatSession? {
        return sessions.find { it.id == currentSessionId.value }
    }

    // Kirim Pesan
    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        // Cek apakah API Key sudah diatur
        if (apiKey.value.isBlank()) {
            settingsErrorMessage.value = "⚠️ API Key belum diisi! Silakan masukkan API Key OpenRouter kamu terlebih dahulu."
            showSettingsDialog.value = true
            return
        }

        val currentSession = getCurrentSession() ?: return

        // Beri judul otomatis berdasarkan pesan pertama
        if (currentSession.messages.isEmpty()) {
            currentSession.title = if (userText.length > 22) userText.take(22) + "..." else userText
        }

        val userMessage = Message(role = "user", content = userText)
        currentSession.messages.add(userMessage)

        isLoading.value = true

        viewModelScope.launch {
            try {
                val request = ChatRequest(
                    model = selectedModel.value.id,
                    messages = currentSession.messages.toList()
                )

                val response = ApiClient.apiService.sendChat(
                    apiKey = "Bearer ${apiKey.value}",
                    request = request
                )

                val botReply = response.choices?.firstOrNull()?.message?.content
                    ?: "Maaf, tidak ada respon dari AI."

                currentSession.messages.add(Message(role = "assistant", content = botReply))

            } catch (e: Exception) {
                currentSession.messages.add(Message(role = "assistant", content = "Error: ${e.localizedMessage}"))
            } finally {
                isLoading.value = false
            }
        }
    }
}
