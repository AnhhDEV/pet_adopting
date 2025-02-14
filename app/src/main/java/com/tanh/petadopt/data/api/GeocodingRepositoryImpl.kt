package com.tanh.petadopt.data.api

import com.tanh.petadopt.domain.api.Address
import com.tanh.petadopt.domain.model.Result
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class GeocodingRepositoryImpl @Inject constructor(
    private val geocodingApi: GeocodingApi
) : GeocodingRepository {

    override suspend fun getCoordinate(query: String, accessToken: String): Result<Address, String> {
        return try {
            val response = geocodingApi.getCoordinate(query = query, accessToken = accessToken)
            Result.Success(response)
        } catch (e: HttpException) {
            Result.Error("API error: ${e.message()}")
        } catch(e: IOException) {
            Result.Error("Network error: ${e.message}")
        } catch(e: Exception) {
            Result.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

}