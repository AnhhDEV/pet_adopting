package com.tanh.petadopt.presentation.detail_message

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import com.mapbox.maps.extension.style.expressions.dsl.generated.any
import com.tanh.petadopt.MainActivity
import com.tanh.petadopt.R
import com.tanh.petadopt.core.util.TestTags
import com.tanh.petadopt.data.AzureBlobStorage
import com.tanh.petadopt.data.ChatRepository
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.di.AppModule
import com.tanh.petadopt.domain.model.Message
import com.tanh.petadopt.domain.model.Result
import com.tanh.petadopt.ui.theme.PetAdoptTheme
import com.tanh.petadopt.util.Util
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class MessageScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @MockK
    val mockRepository: ChatRepository = mockk(relaxed = true)

    private lateinit var viewModel: MessageViewModel

    @MockK
    val googleAuth: GoogleAuthUiClient = mockk()

    @MockK
    val azure: AzureBlobStorage = mockk()

    @Before
    fun setUp() {
        hiltRule.inject()

        val fakeMessages = listOf<Message>()

        coEvery { googleAuth.getSignedInUser()?.userId } returns USER_ID

        coEvery {
                mockRepository.createMessage(any(), any())
        } returns Unit

        coEvery {
            mockRepository.getMessages(CHAT_ID)
        } returns flow {
            emit(Result.Success(fakeMessages))
        }
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                val navController = rememberNavController()
                viewModel = MessageViewModel(googleAuth, mockRepository, azure)
                PetAdoptTheme {
                    NavHost(navController = navController, startDestination = Util.MESSENGER) {
                        composable(route = Util.MESSENGER) {
                            MessageScreen(
                                viewModel = viewModel,
                                chatId = CHAT_ID,
                                receiverId = RECEIVER_ID
                            ) {
                                navController.navigate(it.route)
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun enterMessageScreen_showInputMessage() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        //type a message
        composeRule.onNodeWithTag(TestTags.INPUT_MESSAGE_TEXT_FIELD).performTextInput("hello-world")
        composeRule.onNodeWithContentDescription(context.getString(R.string.send_message))
            .performClick()
        composeRule.onNodeWithText("hello-world").assertIsDisplayed()
    }

    companion object {
        const val USER_ID = "FakeUserId"
        const val CHAT_ID = "FakeChatId"
        const val RECEIVER_ID = "FakeReceiverId"
    }

}