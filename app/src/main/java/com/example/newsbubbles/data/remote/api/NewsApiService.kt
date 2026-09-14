package com.example.newsbubbles.data.remote.api

import com.example.newsbubbles.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    /**
     * Fetches top headlines for a given [category].
     *
     * The category is a request filter only, it is NOT included in the response payload.
     * The caller (repository) is responsible for tagging each article batch with the
     * category that was requested.
     */
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String,
        @Query("country") country: String = "us",
        @Query("pageSize") pageSize: Int = 20,
        @Query("apiKey") apiKey: String
    ): NewsResponseDto
}
