package com.tanh.petadopt.presentation.inbox

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanh.petadopt.domain.model.UserData
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.presentation.components.ChatItem
import com.tanh.petadopt.ui.theme.PetAdoptTheme
import kotlinx.coroutines.launch

@Composable
fun InboxScreen(
    modifier: Modifier = Modifier,
    viewModel: InboxViewModel? = null,
    onNavigate: (OneTimeEvent.Navigate) -> Unit
) {

    val state = viewModel?.state?.collectAsState(initial = InboxUiState())?.value ?: InboxUiState()

    LaunchedEffect(true) {
        viewModel?.channel?.collect { event ->
            when (event) {
                is OneTimeEvent.Navigate -> {
                    onNavigate(event)
                }

                is OneTimeEvent.ShowSnackbar -> TODO()
                is OneTimeEvent.ShowToast -> TODO()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel?.resetState()
        viewModel?.getChats()
    }

    LaunchedEffect(state.chats) {
        if (state.chats.isNotEmpty()) {
            viewModel?.getReceivers()
        }
        Log.d("chat1", state.chats.joinToString { "$it/n" })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Inbox",
                fontSize = 26.sp,
                fontWeight = FontWeight.W700
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(state.chats) { index, chat ->
                ChatItem(
                    chat = chat,
                    receiver = state.receivers.getOrNull(index) ?: UserData()
                ) { chatId ->
                    viewModel?.changeStatusMessage(chatId.first)
                    viewModel?.onNavToDetailChatting(chatId.first, chatId.second)
                }
                Divider(modifier = Modifier
                    .padding(8.dp)
                    .height(1.dp)
                    .background(Color.Gray))
            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun PreviewInboxScreen(modifier: Modifier = Modifier) {
    PetAdoptTheme {
        InboxScreen(modifier = modifier) {

        }
    }
}