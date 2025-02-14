package com.tanh.petadopt.data.api

import com.tanh.petadopt.domain.api.Address
import com.tanh.petadopt.domain.model.Result

interface GeocodingRepository {

    suspend fun getCoordinate(query: String, accessToken: String): Result<Address, String>

}