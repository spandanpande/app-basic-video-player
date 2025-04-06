package com.pubscale.basicvideoplayer.data.network

import com.pubscale.basicvideoplayer.data.viewdata.VideoResponseViewData
import retrofit2.Response
import retrofit2.http.GET

/**
 * An api service to fetch the data from the json using retrofit.
 */
interface VideoApiService {
    @GET("video_url.json")
    suspend fun getVideoUrl(): Response<VideoResponseViewData>
}
