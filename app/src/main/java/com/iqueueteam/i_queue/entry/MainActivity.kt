package com.iqueueteam.i_queue.entry

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iqueueteam.i_queue.entry.databinding.ActivityMainBinding
import com.iqueueteam.i_queue.entry.iqueue.models.IQResponse
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import com.iqueueteam.i_queue.entry.iqueue.models.IQValidationError
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter.apiClient
import com.iqueueteam.i_queue.entry.iqueue.repository.LoginUser
import kotlinx.coroutines.*
import retrofit2.Response


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityMainBinding

    val sharedPref = getSharedPreferences(
        getString(R.string.token_storage), Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Log.d("I-Queue", "Build Type: ${BuildConfig.BUILD_TYPE}")


        binding.sendButton.setOnClickListener {
            login()
        }

    }
    fun login(){
        launch(Dispatchers.IO) {
            // Try catch block to handle exceptions when calling the API.
            try {
                val response = apiClient.doLogin(LoginUser("test@gmail.com","12345"))
                val body:IQResponse<IQUser?,IQValidationError?> = IQueueAdapter.getResponse(response) ?: throw Exception(getString(R.string.error_connecting_server))
                when (true){
                    body.code in 200..299 -> {
                        Log.d("Request","Request Code:${body.code}")
                        if (body.data != null) {
                            if(body.data!!.role == IQUser.Role.ADMIN){
                                val token:String? = body.data!!.token

                            }
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
            Log.d("Error Occurred:", "${e.message}")
            e.printStackTrace()
        }
    }
}