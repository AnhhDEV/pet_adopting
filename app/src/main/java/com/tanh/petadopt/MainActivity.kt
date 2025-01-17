package com.tanh.petadopt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tanh.petadopt.presentation.EntireScreen
import com.tanh.petadopt.presentation.add.AddScreen
import com.tanh.petadopt.presentation.add.AddViewModel
import com.tanh.petadopt.presentation.authentication.Login
import com.tanh.petadopt.presentation.authentication.LoginUiState
import com.tanh.petadopt.presentation.authentication.LoginViewModel
import com.tanh.petadopt.presentation.detail_message.MessageScreen
import com.tanh.petadopt.presentation.detail_message.MessageViewModel
import com.tanh.petadopt.presentation.favorites.FavoriteScreen
import com.tanh.petadopt.presentation.home.Home
import com.tanh.petadopt.presentation.home.HomeViewModel
import com.tanh.petadopt.presentation.inbox.InboxScreen
import com.tanh.petadopt.presentation.inbox.InboxViewModel
import com.tanh.petadopt.presentation.map.MapScreen
import com.tanh.petadopt.presentation.owned_post.OwnedPostScreen
import com.tanh.petadopt.presentation.owned_post.PostViewModel
import com.tanh.petadopt.presentation.pet_detail.DetailScreen
import com.tanh.petadopt.presentation.pet_detail.DetailViewModel
import com.tanh.petadopt.presentation.profile.ProfileScreen
import com.tanh.petadopt.presentation.profile.ProfileViewModel
import com.tanh.petadopt.ui.theme.PetAdoptTheme
import com.tanh.petadopt.util.Util
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetAdoptTheme {

                val loginViewModel = hiltViewModel<LoginViewModel>()
                val homeViewModel = hiltViewModel<HomeViewModel>()
                val detailViewModel = hiltViewModel<DetailViewModel>()
                val addViewModel = hiltViewModel<AddViewModel>()
                val profileViewModel = hiltViewModel<ProfileViewModel>()
                val postViewModel = hiltViewModel<PostViewModel>()
                val inboxViewModel = hiltViewModel<InboxViewModel>()
                val messageViewModel = hiltViewModel<MessageViewModel>()

                val navController = rememberNavController()

                val state = loginViewModel.state.collectAsState(initial = LoginUiState()).value

                var isLoggedIn by remember {
                    mutableStateOf(state.isLoginSuccessful)
                }

                Scaffold(
                    bottomBar = {
                        if (isLoggedIn) {
                            EntireScreen(
                                viewModel = homeViewModel,
                                navController = navController
                            )
                        }
                    }
                ) { paddings ->
                    NavHost(
                        modifier = Modifier.padding(paddings),
                        navController = navController,
                        startDestination = Util.LOG_IN
                    ) {
                        composable(Util.LOG_IN) {
                            isLoggedIn = false
                            Login(
                                viewModel = loginViewModel
                            ) {
                                navController.navigate(it.route) {
                                    launchSingleTop = true
                                    popUpTo(route = Util.LOG_IN) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                        composable(Util.HOME) {
                            isLoggedIn = true
                            Home(viewModel = homeViewModel) {
                                navController.navigate(it.route)
                            }
                        }

                        composable(
                            route = Util.FAVORITE
                        ) {
                            isLoggedIn = true
                            FavoriteScreen(viewModel = homeViewModel) {
                                navController.navigate(it.route)
                            }
                        }

                        composable(
                            route = Util.DETAIL + "/{petId}",
                            arguments = listOf(
                                navArgument(
                                    name = "petId"
                                ) {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ) {
                            isLoggedIn = false
                            val petId = it.arguments?.getString("petId") ?: ""
                            DetailScreen(
                                viewModel = detailViewModel,
                                petId = petId
                            ) { event ->
                                if(event.route == "back") {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(event.route)
                                }
                            }
                        }

                        composable(Util.ADD) {
                            isLoggedIn = false
                            AddScreen(
                                viewModel = addViewModel
                            ) {
                                if(it.route == "back") {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(it.route)
                                }
                            }
                        }

                        composable(Util.PROFILE) {
                            isLoggedIn = true
                            ProfileScreen(
                                viewModel = profileViewModel
                            ) {
                                if(route == Util.LOG_IN) {
                                    inboxViewModel.resetState()
                                }
                                navController.navigate(it.route) {
                                    launchSingleTop = true
                                    popUpTo(route = Util.HOME) {
                                        inclusive = true
                                    }
                                }
                            }
                        }

                        composable(Util.MY_POST) {
                            isLoggedIn = false
                            OwnedPostScreen(
                                viewModel = postViewModel
                            ) {
                                navController.navigate(it.route)
                            }
                        }

                        composable(Util.INBOX) {
                            isLoggedIn = true
                            InboxScreen(
                                viewModel = inboxViewModel
                            ) {
                                navController.navigate(it.route)
                            }
                        }

                        composable(
                            route = Util.MESSENGER + "/{chatId}/{receiverId}",
                            arguments = listOf(
                                navArgument("chatId") {
                                    type = NavType.StringType
                                },
                                navArgument("receiverId") {
                                    type = NavType.StringType
                                }
                            )) {
                            isLoggedIn = false
                            val chatId = it.arguments?.getString("chatId") ?: ""
                            val receiverId = it.arguments?.getString("receiverId") ?: ""
                            MessageScreen(
                                chatId = chatId,
                                receiverId = receiverId,
                                viewModel = messageViewModel
                            ) {route ->
                                if(route.route == "back") {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(route.route)
                                }
                            }
                        }

                        composable(
                            route = Util.MAP
                        ) {
                            MapScreen()
                        }
                    }
                }
            }
        }
    }
}

