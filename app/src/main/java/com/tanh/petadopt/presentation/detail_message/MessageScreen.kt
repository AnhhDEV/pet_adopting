package com.tanh.petadopt.presentation.detail_message

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil3.compose.AsyncImage
import com.tanh.petadopt.R
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.presentation.components.MessageItem
import com.tanh.petadopt.ui.theme.PetAdoptTheme
import com.tanh.petadopt.ui.theme.gradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier,
    viewModel: MessageViewModel? = null,
    chatId: String,
    receiverId: String,
    onNavigate: (OneTimeEvent.Navigate) -> Unit
) {

    val state =
        viewModel?.state?.collectAsState(initial = MessageUiState())?.value ?: MessageUiState()

    var inputMessage by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    WindowCompat.setDecorFitsSystemWindows(
        (context as android.app.Activity).window,
        false
    )

    val pickVisualMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if(uri != null) {
            viewModel?.upImage(uri = uri, chatId = chatId)
        }
    }

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

    LaunchedEffect(state.messages) {
        Log.d("demo1", "receiveId: " + state.receiver)
        Log.d("demo1", "messages:" + state.messages.joinToString { it.toString() })
    }

    LaunchedEffect(Unit) {
        viewModel?.updateChat(chatId = chatId)
        viewModel?.getUserById(id = receiverId)
    }

    LaunchedEffect(state.newChatId) {
        viewModel?.getMessages(chatId = state.newChatId)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradient))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.clickable {
                    viewModel?.onNavToInBox()
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            AsyncImage(
                model = state.receiver?.profilePictureUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = state.receiver?.username ?: "Anonymous",
                fontSize = 20.sp
            )
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .consumeWindowInsets(WindowInsets.ime)
                .padding(16.dp),
            reverseLayout = true
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputMessage,
                        onValueChange = {
                            inputMessage = it
                            viewModel?.onMessageChange(it)
                        },
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)),
                        placeholder = {
                            Text(text = "Type your message")
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            containerColor = Color.White
                        )
                    )
                    IconButton(
                        onClick = {
                            pickVisualMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.imagee),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            if (state.newChatId.isBlank()) {
                                viewModel?.createChat(toId = receiverId)
                            } else {
                                viewModel?.createMessage(chatId = chatId)
                            }
                            inputMessage = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color(0xFFFF5E62),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            if (state.messages.isNotEmpty()) {
                items(state.messages) { message ->
                    MessageItem(
                        modifier = Modifier.padding(vertical = 2.dp),
                        message = message,
                        userId = state.userId
                    )
                }
            } else {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun PreviewMessageScreen(modifier: Modifier = Modifier) {
    PetAdoptTheme {
        MessageScreen(modifier = modifier, chatId = "", receiverId = "") {

        }
    }
}