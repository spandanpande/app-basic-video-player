package com.pubscale.basicvideoplayer.data.repository

import com.pubscale.basicvideoplayer.data.network.VideoApiService
import com.pubscale.basicvideoplayer.data.viewdata.VideoResponseViewData
import retrofit2.Response

class VideoRepository(private val apiService: VideoApiService) {
    suspend fun fetchVideoUrl(): Response<VideoResponseViewData> {
        return apiService.getVideoUrl()
    }
}
