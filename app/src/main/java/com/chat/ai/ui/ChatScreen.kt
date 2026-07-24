package com.chat.ai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chat.ai.ChatViewModel
import com.chat.ai.ThemeMode
import com.chat.ai.data.FREE_AI_MODELS
import com.chat.ai.data.Message
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showModelBottomSheet by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    
    val currentSession = viewModel.getCurrentSession()
    val listState = rememberLazyListState()

    // Auto-scroll ke bawah saat ada pesan baru
    LaunchedEffect(currentSession?.messages?.size) {
        if (!currentSession?.messages.isNullOrEmpty()) {
            listState.animateScrollToItem(currentSession!!.messages.size - 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                // Header Sidebar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Chat AI Pro", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Powered by OpenRouter", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.createNewChat()
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Obrolan Baru")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                Text(
                    text = "Riwayat Obrolan",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    items(viewModel.sessions) { session ->
                        val isSelected = session.id == viewModel.currentSessionId.value
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    session.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 14.sp
                                )
                            },
                            icon = { Icon(Icons.Default.ChatBubbleOutline, contentDescription = null) },
                            selected = isSelected,
                            onClick = {
                                viewModel.selectSession(session.id)
                                scope.launch { drawerState.close() }
                            },
                            badge = {
                                if (isSelected) {
                                    IconButton(
                                        onClick = { viewModel.deleteSession(session.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Footer Sidebar: Settings
                NavigationDrawerItem(
                    label = { Text("Pengaturan", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.settingsErrorMessage.value = null
                        viewModel.showSettingsDialog.value = true
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu Sidebar")
                        }
                    },
                    title = {
                        Surface(
                            onClick = { showModelBottomSheet = true },
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(viewModel.selectedModel.value.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.createNewChat() }) {
                            Icon(Icons.Default.Add, contentDescription = "Chat Baru")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (currentSession?.messages.isNullOrEmpty()) {
                        EmptyStateView(onPromptClick = { prompt -> viewModel.sendMessage(prompt) })
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            items(currentSession!!.messages) { msg ->
                                ChatBubbleItem(message = msg)
                            }
                            if (viewModel.isLoading.value) {
                                item { LoadingBubble() }
                            }
                        }
                    }
                }

                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Tanya apa saja ke AI...", fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 4
                        )

                        FloatingActionButton(
                            onClick = {
                                if (inputText.isNotBlank() && !viewModel.isLoading.value) {
                                    val textToSend = inputText
                                    inputText = ""
                                    viewModel.sendMessage(textToSend)
                                }
                            },
                            modifier = Modifier.size(42.dp),
                            shape = CircleShape,
                            containerColor = if (inputText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (inputText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else Color.Gray,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Kirim", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal Pilih Model AI
    if (showModelBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showModelBottomSheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Pilih Model AI", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))
                FREE_AI_MODELS.forEach { model ->
                    val isSelected = viewModel.selectedModel.value.id == model.id
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                            viewModel.selectedModel.value = model
                            showModelBottomSheet = false
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(model.name, fontWeight = FontWeight.Bold)
                                Text(model.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Settings (API Key & Ganti Tema Dark/Light)
    if (viewModel.showSettingsDialog.value) {
        var tempKey by remember { mutableStateOf(viewModel.apiKey.value) }

        AlertDialog(
            onDismissRequest = { viewModel.showSettingsDialog.value = false },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            title = { Text("Pengaturan") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Tampilkan Peringatan Jika Belum Set API Key
                    if (viewModel.settingsErrorMessage.value != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = viewModel.settingsErrorMessage.value!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // 1. Bagian API Key
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("OpenRouter API Key", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("sk-or-v1-...") }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. Bagian Pilihan Tema (Dark / Light / System)
                    Text("Tampilan Tema", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    val selectedTheme = viewModel.themeMode.value
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = selectedTheme == ThemeMode.SYSTEM,
                            onClick = { viewModel.themeMode.value = ThemeMode.SYSTEM },
                            label = { Text("Sistem", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = selectedTheme == ThemeMode.LIGHT,
                            onClick = { viewModel.themeMode.value = ThemeMode.LIGHT },
                            label = { Text("Terang", fontSize = 12.sp) }
                        )
                        FilterChip(
                            selected = selectedTheme == ThemeMode.DARK,
                            onClick = { viewModel.themeMode.value = ThemeMode.DARK },
                            label = { Text("Gelap", fontSize = 12.sp) }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.apiKey.value = tempKey
                    viewModel.settingsErrorMessage.value = null
                    viewModel.showSettingsDialog.value = false
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showSettingsDialog.value = false }) { Text("Batal") }
            }
        )
    }
}

// Sub-komponen UI
@Composable
fun EmptyStateView(onPromptClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Ada yang bisa kubantu?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        listOf(
            "Tuliskan ide aplikasi Android unik",
            "Buatkan contoh skrip Python sederhana",
            "Jelaskan konsep Kotlin Coroutines"
        ).forEach { suggestion ->
            SuggestionChip(
                onClick = { onPromptClick(suggestion) },
                label = { Text(suggestion, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
fun ChatBubbleItem(message: Message) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp, topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.5.sp,
                lineHeight = 20.sp
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

@Composable
fun LoadingBubble() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sedang berpikir...", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}
