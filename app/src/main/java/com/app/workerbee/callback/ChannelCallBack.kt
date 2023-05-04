package com.app.workerbee.callback

import com.app.workerbee.model.channels.ChannelModel

interface ChannelCallBack {
    fun onClickChannel(position: Int, obj: ChannelModel)
    fun onRemoveChannel(position: Int)
}