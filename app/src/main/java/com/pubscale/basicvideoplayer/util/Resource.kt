package com.pubscale.basicvideoplayer.util

/**
 * A util class to hold the appropriate data for the Success, Loading and Error cases.
 */
sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
}