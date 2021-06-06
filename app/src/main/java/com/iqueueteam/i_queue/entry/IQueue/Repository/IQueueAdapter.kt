package com.iqueueteam.i_queue.entry.IQueue.Repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object IQueueAdapter {
    var e = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build()
    val apiClient:IQueueService = Retrofit.Builder().baseUrl("http://10.0.2.2/api/").client(e)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(IQueueService::class.java)
    public fun <T> getResponse(response: Response<T>): T? {
        if (response.isSuccessful){
            return response.body()
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    val listType = object : TypeToken<T>() {}.type
                    return@launch Gson().fromJson(response.errorBody()?.string(),listType)
                }
            }
        }
        throw Exception("Unexpected Error. Could not parse body")
    }
}