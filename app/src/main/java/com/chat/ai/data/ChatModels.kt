package com.chat.ai.data

import java.util.UUID

data class Message(
    val role: String,
    val content: String
)

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "Obrolan Baru",
    val messages: MutableList<Message> = mutableListOf()
)

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
