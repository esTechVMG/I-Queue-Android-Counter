package com.iqueueteam.i_queue.entry.iqueue.repository

import com.google.gson.Gson
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
    val gsonFactory = GsonConverterFactory.create()
    var loggingLevel:HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor()
            .setLevel(loggingLevel)
        )
        .build()
    val apiClient:IQueueService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2/api/")
        .client(okHttpClient)
        .addConverterFactory(gsonFactory)
        .build()
        .create(IQueueService::class.java)

    fun <T> getResponse(response: Response<T>): T? {
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