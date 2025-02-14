package com.tanh.petadopt.presentation.owned_post

import com.google.common.truth.Truth
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.data.PetRepository
import com.tanh.petadopt.domain.model.Pet
import com.tanh.petadopt.domain.model.Result
import com.tanh.petadopt.domain.model.UserData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private lateinit var testScope: TestScope
    private val auth: GoogleAuthUiClient = mockk()
    private val repository: PetRepository = mockk(relaxed = true)
    private lateinit var viewModel: PostViewModel

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher = dispatcher)
        testScope = TestScope()
        viewModel = PostViewModel(auth, repository)
        coEvery { auth.getSignedInUser()?.userId } returns "FakeUserId"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPets call with correct method`() = testScope.runTest {
        viewModel.getPets()
        advanceUntilIdle()
        coEvery { repository.getPetsByUser("FakeUserId") }
    }

    @Test
    fun `getPets return success UI state updated`() = testScope.runTest {
        val pets = listOf(Pet(), Pet())
        coEvery { repository.getPetsByUser("FakeUserId") } returns flow {
            emit(Result.Success(pets))
        }
        viewModel.getPets()
        advanceUntilIdle()
        val currentState = viewModel.state.value
        Truth.assertThat(currentState.isLoading).isFalse()
        Truth.assertThat(currentState.pets).isEqualTo(pets)
    }

    @Test
    fun `getPets return error UI state updated`() = testScope.runTest {
        coEvery { repository.getPetsByUser("FakeUserId") } returns flow {
            emit(Result.Error(Exception("FakeError")))
        }
        viewModel.getPets()
        advanceUntilIdle()
        val currentState = viewModel.state.value
        Truth.assertThat(currentState.isLoading).isFalse()
        Truth.assertThat(currentState.error).isEqualTo("FakeError")
    }

}