package com.example.projectmdp.ui.module.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projectmdp.ui.theme.ProjectMDPTheme

data class Message(val id: String, val text: String, val isUser: Boolean, val timestamp: Long = System.currentTimeMillis())

@Composable
fun ChatView(modifier: Modifier = Modifier) {
    var messageText by remember { mutableStateOf<String>("") }
    val messages = remember { mutableStateListOf<Message>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages.sortedByDescending { it.timestamp }) { message ->
                MessageBubble(message = message)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                label = { Text("Type a message") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (messageText.isNotBlank()) {
                    val userMessage = Message(id = "user_${System.currentTimeMillis()}", text = messageText, isUser = true)
                    messages.add(userMessage)
                    // Simulate a response after a delay
                    // In a real app, this would come from a backend or another user
                    messages.add(Message(id = "bot_${System.currentTimeMillis()}", text = "Echo: ${userMessage.text}", isUser = false, timestamp = System.currentTimeMillis() + 100)) // Ensure bot message is slightly later
                    messageText = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ProjectMDPTheme {
        ChatView()
    }
}
