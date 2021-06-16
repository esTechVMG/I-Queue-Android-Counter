package com.iqueueteam.i_queue.entry

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.iqueueteam.i_queue.entry.config_storage.SharedPreferencesGson
import com.iqueueteam.i_queue.entry.iqueue.models.IQCommerce
import com.iqueueteam.i_queue.entry.iqueue.models.IQResponse
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import com.iqueueteam.i_queue.entry.iqueue.models.IQValidationError
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter
import com.iqueueteam.i_queue.entry.iqueue.repository.LoginUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun login(
    email:String,
    password:String,
    context: Context,
    onUserAdmin: (user: IQUser) -> Unit,
    onUserNotAdmin: () -> Unit,
    onFailure:()->Unit,
){
    withContext(Dispatchers.IO) {
        // Try catch block to handle exceptions when calling the API.
        try {
            val response = IQueueAdapter.apiClient.doLogin(LoginUser( email,password))
            val body: IQResponse<IQUser?, IQValidationError?> = IQueueAdapter.getResponse(response) ?: throw Exception(context.getString(R.string.error_connecting_server))
            when (true){
                body.code in 200..299 -> {
                    Log.d("Request","Request Code:${body.code}")
                    val user: IQUser = body.data ?: throw Exception("Failed to retrieve user from login Request")
                    if(user.role == IQUser.Role.ADMIN){
                        onUserAdmin(user)
                    }else{
                        onUserNotAdmin()
                    }
                }
                else -> {
                    onFailure()
                }
            }
        } catch (e: Exception){
            toastError(e,context)
        }
    }
}
suspend fun retrieveCommerce(
    user: IQUser,
    context: Context,
    onSuccess:(commerce:IQCommerce)->Unit,
    onFailure: () -> Unit,
){
    withContext(Dispatchers.IO) {
        IQueueAdapter.token = user.token
        // Try catch block to handle exceptions when calling the API.
        try {
            val response = IQueueAdapter.apiClient.getCommerce(user.id)
            val body: IQResponse<IQCommerce, Any?> = IQueueAdapter.getResponse(response) ?: throw Exception(context.getString(R.string.error_connecting_server))
            when (true){
                body.code in 200..299 -> {
                    onSuccess(body.data?: return@withContext)
                }
                else -> {
                    onFailure()
                }
            }
        } catch (e: Exception){
            toastError(e,context)
        }
    }
}
suspend fun toastError(e:Exception, context: Context){
    withContext(Dispatchers.Main) {
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        Log.d(context.getString(R.string.error_unexpected_error), "Error Occurred: ${e.message}")
        e.printStackTrace()
    }
}