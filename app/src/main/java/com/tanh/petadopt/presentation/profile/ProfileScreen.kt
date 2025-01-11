package com.tanh.petadopt.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.tanh.petadopt.R
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.ui.theme.PetAdoptTheme
import com.tanh.petadopt.ui.theme.backgroundItem
import com.tanh.petadopt.ui.theme.itemColor
import com.tanh.petadopt.ui.theme.strokeItem

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel? = null,
    onNavigate: (OneTimeEvent.Navigate) -> Unit
) {

    val state =
        viewModel?.state?.collectAsState(initial = ProfileUiState())?.value ?: ProfileUiState()

    LaunchedEffect(Unit) {
        viewModel?.getUser()
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

    if (state.isLoading == true) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 30.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = state.user?.profilePictureUrl,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.user?.username ?: "No name",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.user?.gmail ?: "No email",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))

            //Thêm pet
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(12.dp)
                    .clickable {
                        viewModel?.onNavToAdd()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            strokeItem,
                            RoundedCornerShape(12.dp)
                        )
                        .background(backgroundItem)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = itemColor,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Add New Pet",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W400
                )
            }

            //Xem post
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(12.dp)
                    .clickable {
                        viewModel?.onNavToMyPost()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            strokeItem,
                            RoundedCornerShape(12.dp)
                        )
                        .background(backgroundItem)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.bookmark),
                        contentDescription = null,
                        tint = itemColor,
                        modifier = Modifier
                            .size(35.dp)
                            .align(Alignment.Center)
                    )

                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "My Post",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W400
                )
            }

            //Xem ưa thích
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(12.dp)
                    .clickable {
                        viewModel?.onNavToFavorite()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            strokeItem,
                            RoundedCornerShape(12.dp)
                        )
                        .background(backgroundItem)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = itemColor,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )

                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Favorites",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W400
                )
            }

            //Inbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(12.dp)
                    .clickable {
                        viewModel?.onNavToInbox()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            strokeItem,
                            RoundedCornerShape(12.dp)
                        )
                        .background(backgroundItem)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.inbox),
                        contentDescription = null,
                        tint = itemColor,
                        modifier = Modifier
                            .size(35.dp)
                            .align(Alignment.Center)
                    )

                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Inbox",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W400
                )
            }

            //Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(12.dp)
                    .clickable {
                        viewModel?.logOut()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            strokeItem,
                            RoundedCornerShape(12.dp)
                        )
                        .background(backgroundItem)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = itemColor,
                        modifier = Modifier
                            .size(35.dp)
                            .align(Alignment.Center)
                    )

                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Logout",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W400
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewProfileScreen(modifier: Modifier = Modifier) {

    PetAdoptTheme {
        ProfileScreen { }
    }

}