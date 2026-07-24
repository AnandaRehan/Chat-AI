package com.chat.ai.data

data class AiModel(
    val id: String,
    val name: String,
    val description: String
)

val FREE_AI_MODELS = listOf(
    AiModel(
        id = "google/gemini-2.0-flash-lite-001:free",
        name = "Gemini 2.0 Flash Lite",
        description = "Sangat cepat & ramah kuota (Default)"
    ),
    AiModel(
        id = "meta-llama/llama-3.3-70b-instruct:free",
        name = "Llama 3.3 70B",
        description = "Sangat pintar untuk obrolan umum"
    ),
    AiModel(
        id = "deepseek/deepseek-r1:free",
        name = "DeepSeek R1",
        description = "Unggul dalam penalaran logika & matematika"
    ),
    AiModel(
        id = "qwen/qwen-2.5-coder-32b-instruct:free",
        name = "Qwen 2.5 Coder",
        description = "Spesialis coding & pemrograman"
    )
)
