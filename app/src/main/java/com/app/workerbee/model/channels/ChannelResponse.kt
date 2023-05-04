package com.app.workerbee.model.channels

import com.google.gson.annotations.SerializedName

data class ChannelResponse (

    @SerializedName("kind"          ) var kind          : String?          = null,
    @SerializedName("etag"          ) var etag          : String?          = null,
    @SerializedName("nextPageToken" ) var nextPageToken : String?          = null,
    @SerializedName("regionCode"    ) var regionCode    : String?          = null,
    @SerializedName("pageInfo"      ) var pageInfo      : PageInfo?        = PageInfo(),
    @SerializedName("items"         ) var items         : ArrayList<ChannelModel> = arrayListOf()

)

data class PageInfo (

    @SerializedName("totalResults"   ) var totalResults   : Int? = null,
    @SerializedName("resultsPerPage" ) var resultsPerPage : Int? = null

)


