package com.pubscale.basicvideoplayer.data.viewdata

import com.google.gson.annotations.SerializedName

data class VideoResponseViewData(
    // Used gson to assign a key "url" to the videoUrl, so that value present in json with the key
    // "url" can be stored in the videoUrl.
    @SerializedName("url") val videoUrl: String
)