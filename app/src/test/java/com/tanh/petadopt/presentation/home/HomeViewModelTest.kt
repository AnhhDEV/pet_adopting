package com.tanh.petadopt.presentation.home

import com.google.common.truth.Truth
import com.tanh.petadopt.data.ChatRepository
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.data.PreferenceRepository
import com.tanh.petadopt.domain.dto.PetDto
import com.tanh.petadopt.domain.model.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    private val auth: GoogleAuthUiClient = mockk()
    private val chatRepository: ChatRepository = mockk(relaxed = true)
    private val preferenceRepository: PreferenceRepository = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { auth.getSignedInUser()?.userId } returns USER_ID
        viewModel = HomeViewModel(auth, chatRepository, preferenceRepository)
    }

    @Test
    fun `getAllPets return success result`() = runTest {
        val fakePets = listOf(PetDto())
        coEvery { preferenceRepository.getPreferenceByUser(USER_ID) } returns flow {
            emit(Result.Success(fakePets))
        }
        viewModel.getAllPets()
        advanceUntilIdle()
        val currentState = viewModel.state.value
        Truth.assertThat(currentState.isLoading).isFalse()
        Truth.assertThat(currentState.pets).isEqualTo(fakePets)
        Truth.assertThat(currentState.errorMessage).isNull()
    }

    companion object {
        private const val USER_ID = "fakeUserId"
    }

}