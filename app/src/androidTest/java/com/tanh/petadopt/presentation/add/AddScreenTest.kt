package com.tanh.petadopt.presentation.add

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.common.truth.Truth
import com.tanh.petadopt.data.AzureBlobStorage
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.data.PetRepository
import com.tanh.petadopt.data.api.GeocodingApi
import com.tanh.petadopt.di.AppModule
import com.tanh.petadopt.domain.model.Result
import com.tanh.petadopt.ui.theme.PetAdoptTheme
import com.tanh.petadopt.util.Util
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class AddScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val auth: GoogleAuthUiClient = mockk()
    private val repository: PetRepository = mockk(relaxed = true)
    private val azure: AzureBlobStorage = mockk()
    private val geocoding: GeocodingApi = mockk()

    private lateinit var viewModel: AddViewModel

    @Before
    fun setup() {
        hiltRule.inject()

        coEvery { auth.getSignedInUser()?.userId } returns USER_ID

        coEvery { repository.insertPet(
            ownerId = USER_ID,
            name = "TestPet",
            age = 1.0,
            weight = 1.0,
            breed = "TestBreed",
            category = "TestCategory",
            gender = true,
            photoUrl = "TestUrl",
            address = "TestAddress",
            about = "TestAbout",
            longitude = 1.0,
            latitude = 1.0
        ) } returns Result.Success(true)

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                val navController = rememberNavController()
                PetAdoptTheme {
                    viewModel = AddViewModel(auth, repository, azure, geocoding)
                    NavHost(navController = navController, startDestination = Util.ADD) {
                        composable(route = Util.ADD) {
                            AddScreen(viewModel = viewModel) {
                                navController.navigate(it.route)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun nameStateIsUpdated_userType() {
        composeRule.onNodeWithTag("NAME").performTextInput("test-pet")
        viewModel.onInsertPet()
        val currentState = viewModel.state.value
//        composeRule.onNodeWithText("Photo cannot be empty").assertIsDisplayed()
        Truth.assertThat(currentState.name).isEqualTo("test-pet")
    }

    companion object {
        private const val USER_ID = "FakeUserId"
        private const val PET_ID = "FakePetId"
    }

}