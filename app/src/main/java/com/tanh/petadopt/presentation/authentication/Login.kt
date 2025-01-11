package com.tanh.petadopt.presentation.authentication

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tanh.petadopt.R
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.presentation.OneTimeEvent
import com.tanh.petadopt.ui.theme.Yellow100
import com.tanh.petadopt.ui.theme.Yellow80
import kotlinx.coroutines.launch

@Composable
fun Login (
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel? = null,
    navController: (OneTimeEvent.Navigate) -> Unit
) {

    val state = viewModel?.state?.collectAsState(initial = LoginUiState())?.value ?: LoginUiState()
    val context = LocalContext.current

    //Check already login
    LaunchedEffect(Unit) {
        val currentUser = viewModel?.getCurrentUser()
        if(currentUser != null) {
            viewModel.onNavToHome()
        }
    }

    //Lắng nghe onetimeevent
    LaunchedEffect(true) {
        viewModel?.channel?.collect {event ->
            when(event) {
                is OneTimeEvent.Navigate -> {
                    navController(event)
                }
                is OneTimeEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel?.onGetIntent(result = result)
    }

    LaunchedEffect(state.isLoginSuccessful) {

        viewModel?.loginSuccessfully()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.login),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
                .height(450.dp)
                .align(Alignment.Start)
                .aspectRatio(4f / 5f)
        )

        Text(
            text = "Ready to make a new friend?",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)
                .padding(horizontal = 40.dp)
        )

        Text(
            text = "Let's adopt the pet which you like and make there life happy again",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Button(
            onClick = {
                viewModel?.onLogin(launcher = launcher)
            },
            colors = ButtonDefaults.buttonColors(Yellow100),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 100.dp)
                .height(50.dp)
        ) {
            Text(
                text = "Get started",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 20.sp
            )
        }

    }

}
