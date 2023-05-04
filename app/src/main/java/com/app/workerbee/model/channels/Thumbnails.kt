package com.app.workerbee.model.channels

import com.google.gson.annotations.SerializedName

data class Thumbnails (

    @SerializedName("default" ) var default : UrlModel? = UrlModel(),
    @SerializedName("medium"  ) var medium  : UrlModel? = UrlModel(),
    @SerializedName("high"    ) var high    : UrlModel? = UrlModel()

)
