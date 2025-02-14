package com.tanh.petadopt.data.api

import com.google.common.truth.Truth
import com.tanh.petadopt.domain.api.Address
import com.tanh.petadopt.domain.api.Feature
import com.tanh.petadopt.domain.model.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class GeocodingRepositoryTest {

    private lateinit var repository: GeocodingRepositoryImpl
    private lateinit var testScope: TestScope
    private val api: GeocodingApi = mockk()

    @Before
    fun setUp() {
        testScope = TestScope()
        repository = GeocodingRepositoryImpl(api)
    }

    @Test
    fun `getCoordinate should return error when API returns HTTPException`() = testScope.runTest {
        coEvery { api.getCoordinate("NewYork", "faketoken") } throws HttpException(mockk(relaxed = true))
        val result = repository.getCoordinate("NewYork", "faketoken")
        advanceUntilIdle()
        Truth.assertThat(result).isInstanceOf(Result.Error::class.java)
        Truth.assertThat(result).isEqualTo(Result.Error("API error: "))
    }

    @Test
    fun `getCoordinate should return error when API returns IOException`() = testScope.runTest {
        coEvery { api.getCoordinate("NewYork", "faketoken") } throws IOException("No internet connection")
        val result = repository.getCoordinate("NewYork", "faketoken")
        advanceUntilIdle()
        Truth.assertThat(result).isInstanceOf(Result.Error::class.java)
        Truth.assertThat(result).isEqualTo(Result.Error("Network error: No internet connection"))
    }

}