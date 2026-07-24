package com.chat.ai.data

enum class Provider {
    OPEN_ROUTER,
    GEMINI
}

data class AiModel(
    val id: String,
    val name: String,
    val description: String,
    val provider: Provider
)

val FREE_AI_MODELS = listOf(
    // --- GOOGLE GEMINI DIRECT API ---
    AiModel(
        id = "gemini-3.6-flash",
        name = "Gemini 3.6 Flash (Direct)",
        description = "Resmi dari Google API",
        provider = Provider.GEMINI
    ),
    AiModel(
        id = "gemini-2.0-flash",
        name = "Gemini 2.0 Flash (Direct)",
        description = "Resmi dari Google API: Super cepat, ramah kuota & gratis",
        provider = Provider.GEMINI
    ),
    AiModel(
        id = "gemini-1.5-flash",
        name = "Gemini 1.5 Flash (Direct)",
        description = "Resmi dari Google API: Sangat stabil & efisien",
        provider = Provider.GEMINI
    ),

    // --- OPENROUTER MODELS ---
    AiModel(
        id = "google/gemma-4-31b-it:free",
        name = "Gemma 4 31B",
        description = "Paling pintar untuk obrolan umum & instruksi (OpenRouter)",
        provider = Provider.OPEN_ROUTER
    ),
    AiModel(
        id = "cohere/north-mini-code:free",
        name = "Cohere North Code",
        description = "Spesialis coding & debugging program (OpenRouter)",
        provider = Provider.OPEN_ROUTER
    ),
    AiModel(
        id = "nvidia/nemotron-3-nano-omni-30b-a3b-reasoning:free",
        name = "Nemotron Reasoning 30B",
        description = "Unggul dalam penalaran, matematika & logika (OpenRouter)",
        provider = Provider.OPEN_ROUTER
    ),
    AiModel(
        id = "openrouter/free",
        name = "OpenRouter Auto Free",
        description = "Otomatis memilihkan model gratis yang sedang aktif",
        provider = Provider.OPEN_ROUTER
    )
)
