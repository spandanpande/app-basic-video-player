package com.pubscale.basicvideoplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pubscale.basicvideoplayer.data.network.VideoApiService
import com.pubscale.basicvideoplayer.util.Resource
import com.pubscale.basicvideoplayer.data.repository.VideoRepository
import com.pubscale.basicvideoplayer.data.viewdata.VideoResponseViewData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VideoViewModel : ViewModel() {

    private val _videoResource = MutableLiveData<Resource<VideoResponseViewData>>()
    val videoResource: LiveData<Resource<VideoResponseViewData>> = _videoResource

    private val repository: VideoRepository

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/greedyraagava/test/refs/heads/main/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create a repository.
        repository = VideoRepository(retrofit.create(VideoApiService::class.java))
    }

    fun loadVideoUrl() {
        viewModelScope.launch {
            _videoResource.postValue(Resource.Loading())

            val maxRetries = 2
            val retryDelay = 1000L
            var attempt = 0

            while (attempt <= maxRetries) {
                try {
                    // Fetch the video url from network.
                    val response = repository.fetchVideoUrl()

                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            _videoResource.postValue(Resource.Success(VideoResponseViewData(body.videoUrl)))
                            return@launch
                        } ?: _videoResource.postValue(Resource.Error("Invalid response data"))
                    } else {
                        _videoResource.postValue(Resource.Error("Server error: ${response.code()}"))
                    }
                    return@launch
                } catch (e: Exception) {
                    // Retry logic
                    attempt++
                    if (attempt > maxRetries) {
                        // Retries are exhausted, so return an error.
                        _videoResource.postValue(Resource.Error("Failed after $attempt tries: ${e.localizedMessage}"))
                    } else {
                        // Use 1 second delay between the retries.
                        delay(retryDelay)
                    }
                }
            }
        }
    }
}
