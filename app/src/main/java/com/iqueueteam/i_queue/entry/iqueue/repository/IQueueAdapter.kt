package com.iqueueteam.i_queue.entry.iqueue.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iqueueteam.i_queue.entry.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object IQueueAdapter {
    const val baseUrl:String = "http://10.144.110.119/i-Queue-BackEnd/public/api/"
    internal var token:String? = null
        get() = field
        set(value) {
            field = value
        }
    private val gsonFactory: GsonConverterFactory = GsonConverterFactory.create()
    var loggingLevel:()->(HttpLoggingInterceptor.Level) = {
        if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        }else{
            HttpLoggingInterceptor.Level.NONE
        }
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor()
            .setLevel(loggingLevel())
        ).addInterceptor(Interceptor { chain ->
            if (token != null){
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                return@Interceptor chain.proceed(newRequest)
            }else{
                return@Interceptor chain.proceed(chain.request())
            }
        })
        .build()


    val apiClient:IQueueService = Retrofit.Builder()
        .baseUrl(baseUrl)
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