package com.app.workerbee.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.workerbee.callback.ChannelCallBack
import com.app.workerbee.databinding.ItemSavedChannelBinding
import com.app.workerbee.model.channels.ChannelModel
import com.app.workerbee.utility.Utils

class SavedChannelAdapter(
    private val mContext: Context,
    private var channelList: ArrayList<ChannelModel>,
    private var channelCallBack: ChannelCallBack
) : RecyclerView.Adapter<SavedChannelAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemSavedChannelBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return channelList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(channelList[position])
    }

    inner class ViewHolder(private val binding: ItemSavedChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRemove.setOnClickListener {
                channelCallBack.onRemoveChannel(layoutPosition)
            }
        }

        fun bind(obj: ChannelModel) {

            Utils.loadImage(
                mContext,
                Utils.checkNull(obj.snippet?.thumbnails?.medium?.url),
                binding.imageView
            )

        }
    }
}
