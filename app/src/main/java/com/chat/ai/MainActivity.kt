package com.chat.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.chat.ai.ui.ChatScreen

class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()
    private val apiKey = "sk-or-v1-xxxxxxxxxxxxxxxxxxxx" // Masukkan API Key OpenRouter kamu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    ChatScreen(viewModel = viewModel, apiKey = apiKey)
                }
            }
        }
    }
}
