package com.tanh.petadopt.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.tanh.petadopt.presentation.home.Home
import com.tanh.petadopt.presentation.home.HomeViewModel
import com.tanh.petadopt.ui.theme.FilledMap
import com.tanh.petadopt.ui.theme.OutlinedMap

@Composable
fun EntireScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel? = null,
    navController: NavController
) {

    val currentDestination =
        navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)

    val state = viewModel?.totalUnreadMessage?.collectAsState(initial = 0)?.value ?: 0

    val items = remember {
        mutableStateListOf(
            BottomNavigationItem(
                title = "Home",
                selectedIcon = Icons.Default.Home,
                unselectedIcon = Icons.Outlined.Home,
                hasNews = false
            ),
            BottomNavigationItem(
                title = "Favorite",
                selectedIcon = Icons.Default.Favorite,
                unselectedIcon = Icons.Outlined.FavoriteBorder,
                hasNews = false
            ),
            BottomNavigationItem(
                title = "Map",
                selectedIcon = FilledMap,
                unselectedIcon = OutlinedMap,
                hasNews = false
            ),
            BottomNavigationItem(
                title = "Inbox",
                selectedIcon = Icons.Default.Email,
                unselectedIcon = Icons.Outlined.MailOutline,
                hasNews = false
            ),
            BottomNavigationItem(
                title = "Profile",
                selectedIcon = Icons.Default.Person,
                unselectedIcon = Icons.Outlined.Person,
                hasNews = false
            ),
        )
    }

    var isActive by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        if(!isActive) {
            isActive = true
            viewModel?.getTotalUnreadMessage()
        }
    }

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            val isSelected = currentDestination.value?.destination?.route == item.title
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.title) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = {
                    Text(text = item.title)
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if(state > 0 && item.title == "Inbox") {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text("$state")
                                }
                            }
                        }
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = if (isSelected) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            },
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}


data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    var hasNews: Boolean,
    var badgeCount: Int? = null
)