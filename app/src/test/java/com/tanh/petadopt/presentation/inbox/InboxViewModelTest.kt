package com.tanh.petadopt.presentation.inbox

import com.google.common.truth.Truth
import com.tanh.petadopt.data.ChatRepository
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.domain.model.Chat
import com.tanh.petadopt.domain.model.Result
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.Dispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class InboxViewModelTest {

    private lateinit var testScope: TestScope
    private lateinit var viewModel: InboxViewModel
    private val chatRepository: ChatRepository = mockk(relaxed = true)
    private val auth: GoogleAuthUiClient = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { auth.getSignedInUser()?.userId } returns USER_ID
        viewModel = InboxViewModel(chatRepository = chatRepository, auth = auth)
        testScope = TestScope(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `changeStatusMessage method calls chatRepository with correct methods`() = testScope.runTest {
        val chatId = "fakeChatId"
        viewModel.changeStatusMessage(chatId = chatId)
        advanceUntilIdle()
        coVerify { chatRepository.changeStatusMessage(chatId = chatId, userId = USER_ID) }
    }

    @Test
    fun `getChats should update UI state with success path`() = testScope.runTest {
        val fakeChats = listOf(Chat(chatId = "fakeChatId", lastMessage = "fakeLastMessage"), Chat(chatId = "fakeChatId2", lastMessage = "fakeLastMessage2"))
        every { chatRepository.getChats(USER_ID) } returns flow {
            emit(Result.Success(fakeChats))
        }
        viewModel.getChats()
        advanceUntilIdle()
        val currentState = viewModel.state.value
        Truth.assertThat(currentState.isLoading).isFalse()
        Truth.assertThat(currentState.chats).isEqualTo(fakeChats)
        Truth.assertThat(currentState.error).isNull()
    }

    @Test
    fun `getChats should update UI state with error path`() = testScope.runTest {
        val fakeError = Result.Error(Exception("fakeError"))
        every { chatRepository.getChats(USER_ID) } returns flow {
            emit(fakeError)
        }
        viewModel.getChats()
        advanceUntilIdle()
        val currentState = viewModel.state.value
        Truth.assertThat(currentState.isLoading).isFalse()
        Truth.assertThat(currentState.error).isEqualTo("fakeError")
    }

    companion object {
        private const val USER_ID = "fakeUserId"
    }

}