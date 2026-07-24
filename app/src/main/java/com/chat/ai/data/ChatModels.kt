package com.chat.ai.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.UUID

data class Message(
    val role: String,
    val content: String
)

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "Obrolan Baru",
    val messages: SnapshotStateList<Message> = mutableStateListOf()
)

// --- OpenRouter Data Models ---
data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

data class ChatResponse(
    val choices: List<Choice>?
)

data class Choice(
    val message: Message?
)

// --- Google Gemini Data Models ---
data class GeminiPart(
    val text: String
)

data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)
