package com.iqueueteam.i_queue.entry.iqueue.repository

import com.iqueueteam.i_queue.entry.iqueue.models.*
import retrofit2.Response
import retrofit2.http.*


interface IQueueService {
    @Headers("Accept: application/json","Content-Type: application/json")
    //@FormUrlEncodedAny
    //@Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("login")
    suspend  fun doLogin(@Body user: LoginUser): Response<IQResponse<IQUser?,IQValidationError?>>
    @POST("queue-entry-mail")
    suspend  fun doentrymail(@Body user: IqueueEntryMail): Response<IQResponse<IQEntryMail?,IQValidationError?>>
    @GET("users/{userId}/commerce")
    suspend fun getCommerce(@Path("userId") userId:Int): Response<IQResponse<IQCommerce,Any?>>
}