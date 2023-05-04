package com.app.workerbee.api

import com.app.workerbee.utility.Constant
import com.app.workerbee.utility.Constant.Istokenset
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var apiClient: ApiInterface? = null

    //Create a new Interceptor.
    private var headerAuthorizationInterceptor: Interceptor = Interceptor { chain ->
        var request = chain.request()
        val headers = request.headers.newBuilder().add("Authorization", "Bearer " +  Constant.authentication_header_token).build()
        request = request.newBuilder().headers(headers).build()
        chain.proceed(request)
    }

    val client: ApiInterface
        get() {
            if (apiClient == null || !Istokenset ) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                lateinit var defaultHttpClient :OkHttpClient

                if(Constant.authentication_header_token.isEmpty()){
                    Istokenset = false
                     defaultHttpClient = OkHttpClient.Builder()
                        .connectTimeout(500, TimeUnit.SECONDS)
                        .readTimeout(500, TimeUnit.SECONDS)
                        .writeTimeout(500, TimeUnit.SECONDS)
                        .addInterceptor(loggingInterceptor)
                        .build()
                } else {
                    Istokenset = true
                    defaultHttpClient = OkHttpClient.Builder()
                        .connectTimeout(500, TimeUnit.SECONDS)
                        .readTimeout(500, TimeUnit.SECONDS)
                        .writeTimeout(500, TimeUnit.SECONDS)
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor(headerAuthorizationInterceptor)
                        .build()
                }

                apiClient = Retrofit.Builder().baseUrl(APIConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(defaultHttpClient)
                    .build()
                    .create(ApiInterface::class.java)
            }
            return apiClient!!
        }
}
