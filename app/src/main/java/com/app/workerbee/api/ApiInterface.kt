package com.app.workerbee.api

import com.app.workerbee.model.channels.ChannelModel
import com.app.workerbee.model.channels.ChannelResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET
    fun getSearchedChannels(@Url str: String): Observable<Response<ChannelResponse>>

}