package com.iqueueteam.i_queue.entry.IQueue.Repository

import com.iqueueteam.i_queue.entry.IQueue.Models.IQCommerce
import com.iqueueteam.i_queue.entry.IQueue.Models.IQResponse
import com.iqueueteam.i_queue.entry.IQueue.Models.IQUser
import com.iqueueteam.i_queue.entry.IQueue.Models.IQValidationError
import retrofit2.Response
import retrofit2.http.*


interface IQueueService {
    @Headers("Accept: application/json","Content-Type: application/json")
    //@FormUrlEncodedAny
    //@Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("login")
    suspend  fun doLogin(@Body user: LoginUser): Response<IQResponse<IQUser?,IQValidationError?>>

    @GET("commerces")
    suspend fun getCommerce(): Response<IQResponse<Array<IQCommerce>,Any?>>

}