package com.tanh.petadopt.presentation.owned_post

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.presentation.components.OwnedPetItem
import com.tanh.petadopt.presentation.components.PetItem
import com.tanh.petadopt.ui.theme.PetAdoptTheme

@Composable
fun OwnedPostScreen (
    modifier: Modifier = Modifier,
    viewModel: PostViewModel? = null,
    onNavigate: (OneTimeEvent.Navigate) -> Unit
) {

    val state = viewModel?.state?.collectAsState(initial = PostUiScreen())?.value ?: PostUiScreen()

    LaunchedEffect(true) {
        viewModel?.channel?.collect {
            when(it) {
                is OneTimeEvent.Navigate -> {
                    onNavigate(it)
                }
                is OneTimeEvent.ShowSnackbar -> TODO()
                is OneTimeEvent.ShowToast -> TODO()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel?.getPets()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel?.onNavToProfile()
                },
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "My Post",
                fontSize = 22.sp,
                fontWeight = FontWeight.W400,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if(state.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    items(state.pets) { pet ->
                        OwnedPetItem(
                            pet = pet
                        ) { petId ->
                            viewModel?.onNavToDetail(petId = petId)
                        }
                    }
                }
            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun PreviewScreen (modifier: Modifier = Modifier) {
    PetAdoptTheme {
        OwnedPostScreen {  }
    }
}