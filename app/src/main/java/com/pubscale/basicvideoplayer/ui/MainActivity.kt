package com.pubscale.basicvideoplayer.ui

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pubscale.basicvideoplayer.R
import com.pubscale.basicvideoplayer.util.Resource
import com.pubscale.basicvideoplayer.viewmodel.VideoViewModel
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var progressBar: ProgressBar? = null

    private val viewModel: VideoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        progressBar = findViewById(R.id.progress_bar)
        player = ExoPlayer.Builder(this).build().also {
            playerView?.player = it
        }

        // Add observer for the video and fetch the video from network
        observeVideoUrl()
        viewModel.loadVideoUrl()
    }

    /**
     * onUserLeaveHint() is called when the user is about to leave the activity, like when pressing
     * the home button. Hence, it is an ideal place to enable Picture in Picture mode.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPictureInPictureModeCompat()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        playerView?.useController = !isInPictureInPictureMode
        supportActionBar?.let {
            if (isInPictureInPictureMode) it.hide() else it.show()
        }
    }

    override fun onRestart() {
        super.onRestart()
        player?.play()
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        player?.release()
        super.onDestroy()
    }

    private fun observeVideoUrl() {
        viewModel.videoResource.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar?.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    progressBar?.visibility = View.GONE
                    playVideoFromUrl(resource.data.videoUrl)
                }

                is Resource.Error -> {
                    progressBar?.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Plays the video from the url
     *
     * @param url URL of the video to be played
     */
    private fun playVideoFromUrl(url: String) {
        val mediaItem = MediaItem.fromUri(url.toUri())
        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    /**
     * Enters the screen into Picture in Picture mode
     */
    private fun enterPictureInPictureModeCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val aspectRatio = Rational(16, 9)
            val pipParams = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
                .build()
            enterPictureInPictureMode(pipParams)
        }
    }
}