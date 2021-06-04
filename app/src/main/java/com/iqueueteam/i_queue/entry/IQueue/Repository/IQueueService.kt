package com.iqueueteam.i_queue.entry.IQueue.Repository

import com.iqueueteam.i_queue.entry.IQueue.Models.IQResponse
import com.iqueueteam.i_queue.entry.IQueue.Models.IQUser
import com.iqueueteam.i_queue.entry.IQueue.Models.IQValidationError
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST


interface IQueueService {
    @Headers("Accept: application/json","Content-Type: application/json")
    //@FormUrlEncoded
    //@Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("login")
    suspend  fun doLogin(@Body user: LoginUser): Response<IQResponse<IQUser?,IQValidationError?>>

}