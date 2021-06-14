package com.iqueueteam.i_queue.entry

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iqueueteam.i_queue.entry.config_storage.SharedPreferencesGson
import com.iqueueteam.i_queue.entry.databinding.ActivityMainBinding
import com.iqueueteam.i_queue.entry.iqueue.models.IQResponse
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import com.iqueueteam.i_queue.entry.iqueue.models.IQValidationError
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter.apiClient
import com.iqueueteam.i_queue.entry.iqueue.repository.LoginUser
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferencesGson:SharedPreferencesGson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferencesGson = SharedPreferencesGson(this)
        Log.d("I-Queue", "Build Type: ${BuildConfig.BUILD_TYPE}")
        try {
            val iqUser:IQUser = sharedPreferencesGson.getObjectFromSharedPref(IQUser::class,getString(R.string.user_info_storage))
            Log.d(getString(R.string.app_name),"Login Credentials retreived. Trying to get commerce and queue info from server")
            //TODO request data and pass to next screen in that case
        }catch (e:Exception){
            Log.d(getString(R.string.app_name),"Could not retrieve user data")
        }

        binding.sendButton.setOnClickListener {
            login()
        }

    }
    private fun login(){
        launch(Dispatchers.IO) {
            // Try catch block to handle exceptions when calling the API.
            try {
                //TODO Make validations
                val response = apiClient.doLogin(LoginUser("" ,"12345"))
                val body:IQResponse<IQUser?,IQValidationError?> = IQueueAdapter.getResponse(response) ?: throw Exception(getString(R.string.error_connecting_server))
                when (true){
                    body.code in 200..299 -> {
                        Log.d("Request","Request Code:${body.code}")
                        val user:IQUser = body.data ?: throw Exception("Failed to retrieve user from login Request")
                        if(body.data!!.role == IQUser.Role.ADMIN){
                            sharedPreferencesGson.setObjectToSharedPref(body.data!!,getString(R.string.user_info_storage));
                            Log.d(getString(R.string.app_name),"User Logged In successfully")

                        }else{
                            Log.d(getString(R.string.app_name),"User logged in but its not and admin user")
                            //TODO Make a popup saying that is not an admin user
                        }
                    }
                    else -> {
                        throw  Exception("Unexpected Error")
                    }
                }
            } catch (e: Exception){
                toastError(e,this@MainActivity)
            }
        }
    }
    fun toastError(e:Exception,context: Context){
        launch(Dispatchers.Main) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            Log.d(getString(R.string.error_unexpected_error), "Error Occurred: ${e.message}")
            e.printStackTrace()
        }
    }
}