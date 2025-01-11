package com.tanh.petadopt.presentation.pet_detail

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.tanh.petadopt.R
import com.tanh.petadopt.domain.dto.PetDto
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.ui.theme.MyBlue
import com.tanh.petadopt.ui.theme.Yellow60
import kotlinx.coroutines.delay

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel? = null,
    petId: String,
    onBack: (OneTimeEvent.Navigate) -> Unit
) {

    val state =
        viewModel?.state?.collectAsState(initial = DetailUiState())?.value ?: DetailUiState()

    val isError = state.error.isNotEmpty()

    var isExpanded by remember {
        mutableStateOf(false)
    }

    val isFavorite = state.isFavorite ?: false

    LaunchedEffect(Unit) {
        viewModel?.getPetByAnimalId(petId)
    }

    LaunchedEffect(state.pet) {
        state.pet?.let { pet ->
            Log.d("detail", "ownerId: ${state.pet.ownerId}")
            viewModel?.getOwner(pet.ownerId ?: "null")
            viewModel?.getChatId(state.pet.ownerId ?: "")
        } ?: run {
            Log.d("test3", "pet is null")
        }
    }

    LaunchedEffect(state.isFavorite) {
        viewModel?.getPetByAnimalId(petId)
    }

    val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
    val color = if (isFavorite) Color.Red else Color.Red

    LaunchedEffect(key1 = true) {
        viewModel?.channel?.collect { event ->
            when (event) {
                is OneTimeEvent.Navigate -> {
                    onBack(event)
                }
                is OneTimeEvent.ShowSnackbar -> {}
                is OneTimeEvent.ShowToast -> {}
            }
        }
    }

    if (state.isLoading == true) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.height(300.dp)
            ) {
                AsyncImage(
                    model = state.pet?.photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = {
                        viewModel?.navToHome()
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = state.pet?.name ?: "Unknown",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.pet?.address ?: "No address",
                        color = Color.Gray,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp
                    )
                }
                IconButton(
                    onClick = {
                        if (isFavorite) {
                            viewModel?.removeFromFavorite(state.pet?.animalId ?: "")
                        } else {
                            viewModel?.addToFavorite(state.pet?.animalId ?: "")
                        }
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .width(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(4.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.age),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Age",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${state.pet?.age.toString()} Years",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .width(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(4.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.breed),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Breed",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            text = state.pet?.breed ?: "null",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .width(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(4.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sex),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sex",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (state.pet?.gender == true) "Male" else "Female",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .width(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(4.dp)
                        .background(Color.White)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.weight),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Weight",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${state.pet?.weight?.toString() ?: "0.0"} Kg",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = "About Avenger",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
                LazyColumn {
                    item {
                        Text(
                            text = state.pet?.about ?: "Nothing",
                            fontWeight = FontWeight.W300,
                            fontSize = 16.sp,
                            overflow = TextOverflow.Clip,
                            maxLines = if(!isExpanded) 3 else Int.MAX_VALUE
                        )
                    }
                    item {
                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )
                    }
                    item {
                        Text(
                            text = if (isExpanded) "Show Less" else "Show More",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400,
                            color = MyBlue,
                            modifier = Modifier.clickable {
                                isExpanded = !isExpanded
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Yellow60, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = state.user?.profilePictureUrl ?: "",
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.clip(CircleShape).size(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.user?.username ?: "Anonymous",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pet owner",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = Yellow60,
                    modifier = Modifier.clickable {
                        val userId = state.pet?.ownerId ?: ""
                        if(!viewModel?.isYourself(userId)!!) {
                            viewModel.onNavToInbox(userId)
                        }
                    }
                )
            }

        }


    }

}

@Preview(showSystemUi = true)
@Composable
fun PreviewDetailScreen(modifier: Modifier = Modifier) {

    DetailScreen(
        petId = "9Rehk26KqM3ZsNcRWWh5"
    ) { }

}