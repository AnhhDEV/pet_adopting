package com.tanh.petadopt.presentation.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.Timestamp
import com.tanh.petadopt.converter.TimeDefinition
import com.tanh.petadopt.domain.model.Chat
import com.tanh.petadopt.domain.model.UserData
import com.tanh.petadopt.ui.theme.PetAdoptTheme

@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    chat: Chat,
    receiver: UserData,
    onItemClick: (Pair<String, String>) -> Unit
) {

    Row(
        modifier = modifier.clickable {
            onItemClick(Pair(chat.chatId ?: "", chat.toId ?: ""))
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = receiver.profilePictureUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = receiver.username ?: "Noname",
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
                maxLines = 1
            )
            Row {
                Text(
                    text = chat.lastMessage ?: "No message",
                    fontSize = 12.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.W300,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = TimeDefinition.formatDate(chat.lastTime ?: Timestamp.now()),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W300,
                    maxLines = 1
                )
            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun PreviewChatItem() {
    PetAdoptTheme {
    }
}