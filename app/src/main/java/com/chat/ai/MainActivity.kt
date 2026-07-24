package com.chat.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.viewModels
import com.chat.ai.ui.ChatScreen

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    // Tempelkan OpenRouter API Key kamu di sini
    private val apiKey = "sk-or-v1-xxxxxxxxxxxxxxxxxxxx" 

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(viewModel: MainViewModel = viewModel()) {
    var textInput by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Auto-scroll ke paling bawah saat ada pesan baru
    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        
        // --- 1. Input API Key ---
        OutlinedTextField(
            value = viewModel.apiKey.value,
            onValueChange = { viewModel.apiKey.value = it },
            label = { Text("OpenRouter API Key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- 2. Dropdown Pemilihan Model ---
        ExposedDropdownMenuBox(
            expanded = expandedDropdown,
            onExpandedChange = { expandedDropdown = !expandedDropdown },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewModel.selectedModel.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Model AI") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false }
            ) {
                viewModel.availableModels.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model) },
                        onClick = {
                            viewModel.selectedModel.value = model
                            expandedDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 3. Area Chat ---
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.messages) { msg ->
                val isUser = msg.role == "user"
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer 
                                             else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.content,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        if (viewModel.isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp))
        }

        viewModel.errorMessage.value?.let { err ->
            Text(text = err, color = Color.Red, modifier = Modifier.padding(4.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 4. Input Prompt & Tombol Kirim ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Ketik pesan...") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val prompt = textInput
                    textInput = ""
                    viewModel.sendMessage(prompt)
                },
                enabled = !viewModel.isLoading.value
            ) {
                Text("Kirim")
            }
        }
    }
}
