package com.app.workerbee.model.channels

import com.google.gson.annotations.SerializedName

data class ChannelModel (

    @SerializedName("kind"    ) var kind    : String?  = null,
    @SerializedName("etag"    ) var etag    : String?  = null,
    @SerializedName("id"      ) var id      : Id?      = Id(),
    @SerializedName("snippet" ) var snippet : Snippet? = Snippet()

)

data class Id (

    @SerializedName("kind"      ) var kind      : String? = null,
    @SerializedName("channelId" ) var channelId : String? = null

)
