package com.example.trimmix

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApiService {
    //Spotify search endpoint
    @GET("v1/search")
    fun searchMusic(
        @Header("Authorization") authorization: String, // Add the Authorization header
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): Call<SpotifySearchResponse>
}