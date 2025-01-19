package com.tanh.petadopt

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.tanh.petadopt.domain.service.MessageNotificationService
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
import com.tanh.petadopt.presentation.map.MapViewModel
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

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.FOREGROUND_SERVICE
                ),
                1
            )
        }

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
                var flag by remember {
                    mutableStateOf(false)
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
                    AnimatedNavHost(
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
                        composable(Util.HOME, enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(1000)
                            )
                        },
                            exitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(1000)
                                )
                            }) {
                            if (!flag) {
                                flag = true
                                Intent(
                                    applicationContext,
                                    MessageNotificationService::class.java
                                ).also {
                                    it.action = MessageNotificationService.Actions.START.toString()
                                    startService(it)
                                }
                            }

                            isLoggedIn = true
                            Home(viewModel = homeViewModel) {
                                navController.navigate(it.route)
                            }
                        }

                        composable(
                            route = Util.FAVORITE,
                            enterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(1000)
                                )
                            },
                            exitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(1000)
                                )
                            }
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
                                if (event.route == "back") {
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
                                if (it.route == "back") {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(it.route)
                                }
                            }
                        }

                        composable(Util.PROFILE,
                            enterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(1000)
                                )
                            },
                            exitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(1000)
                                )
                            }) {
                            isLoggedIn = true
                            ProfileScreen(
                                viewModel = profileViewModel
                            ) {
                                if (it.route == Util.LOG_IN) {
                                    if (flag) {
                                        flag = false
                                        Intent(
                                            applicationContext,
                                            MessageNotificationService::class.java
                                        ).also { intent ->
                                            intent.action =
                                                MessageNotificationService.Actions.STOP.toString()
                                            startService(intent)
                                        }
                                    }
                                }
                                if (route == Util.LOG_IN) {
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

                        composable(Util.INBOX,
                            enterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(1000)
                                )
                            },
                            exitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(1000)
                                )
                            }) {
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
                            ) { route ->
                                if (route.route == "back") {
                                    navController.popBackStack()

                                } else {
                                    navController.navigate(route.route)
                                }
                            }
                        }

                        composable(
                            route = Util.MAP
                        ) {
                            isLoggedIn = true
                            val mapViewModel = hiltViewModel<MapViewModel>()
                            MapScreen(
                                viewModel = mapViewModel
                            ) {
                                navController.navigate(it.route)
                            }
                        }
                    }
                }
            }
        }
    }
}

