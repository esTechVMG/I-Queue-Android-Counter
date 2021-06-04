package com.iqueueteam.i_queue.entry.IQueue.Repository

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object IQueueAdapter {
    var e = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build()
    val apiClient:IQueueService = Retrofit.Builder().baseUrl("http://10.0.2.2/api/").client(e)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(IQueueService::class.java)
}