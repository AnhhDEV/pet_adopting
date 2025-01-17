package com.tanh.petadopt.data.api

import com.tanh.petadopt.domain.api.Address
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    @GET("/search/geocode/v6/forward")
    suspend fun getCoordinate(
        @Query("q") query: String,
        @Query("access_token") accessToken: String
    ): Address

}