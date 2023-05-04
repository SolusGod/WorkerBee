package com.app.workerbee.activity

import android.content.Intent
import android.os.Bundle
import com.app.workerbee.activity.base.BaseActivity
import com.app.workerbee.adapter.SavedChannelAdapter
import com.app.workerbee.adapter.SavedChannelPlaylistAdapter
import com.app.workerbee.callback.ChannelCallBack
import com.app.workerbee.databinding.ActivityHomeBinding
import com.app.workerbee.model.channels.ChannelModel
import com.app.workerbee.utility.Constant
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

class HomeActivity : BaseActivity(), ChannelCallBack {

    private var savedChannelList = ArrayList<ChannelModel>()
    private lateinit var savedChannelAdapter: SavedChannelPlaylistAdapter

    private var player: YouTubePlayer? = null
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedChannelList = myPreferences!!.getChannelList(Constant.SAVED_CHANNEL, ChannelModel::class.java)
        savedChannelAdapter = SavedChannelPlaylistAdapter(mContext, savedChannelList, this)
        binding.rvSavedChannel.adapter = savedChannelAdapter

        lifecycle.addObserver(binding.youtubePlayerView)

        binding.btnChannels.setOnClickListener {
            startActivity(Intent(this, ChannelActivity::class.java))
            finish()
        }

        // Custom design
        /*val listener: YouTubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
            }
        }

        val options: IFramePlayerOptions = IFramePlayerOptions.Builder().controls(0).build()
        binding.youtubePlayerView.initialize(listener, options)*/

/*        binding.youtubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                player = youTubePlayer
                val videoId = videoList[0].videoID
                youTubePlayer.loadVideo(videoId, 0f)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                //Log.d("current_second_video", "onCurrentSecond: $second")
            }

        })*/


    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.release()
    }

    override fun onClickChannel(position: Int, obj: ChannelModel) {

    }

    override fun onRemoveChannel(position: Int) {

    }

}