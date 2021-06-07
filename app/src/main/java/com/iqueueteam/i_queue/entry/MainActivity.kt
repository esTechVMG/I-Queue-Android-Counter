package com.iqueueteam.i_queue.entry

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iqueueteam.i_queue.entry.iqueue.models.IQResponse
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import com.iqueueteam.i_queue.entry.iqueue.models.IQValidationError
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter.apiClient
import com.iqueueteam.i_queue.entry.iqueue.repository.LoginUser
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var sendButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("I-Queue", "Build Type: ${BuildConfig.BUILD_TYPE}")
        sendButton = findViewById(R.id.send_button)
        sendButton.setOnClickListener {
            login()
        }
    }
    fun login(){
        launch(Dispatchers.IO) {
            // Try catch block to handle exceptions when calling the API.
            try {
                val response = apiClient.doLogin(LoginUser("test@gmail.com","12345"))
                val myBody:IQResponse<IQUser?,IQValidationError?>? = IQueueAdapter.getResponse(response)
                myBody?.let {
                    when (true){
                        it.code in 200..299 -> {
                            Log.d("Request","Request Code:${it.code}")
                        }
                        else -> {
                            Log.d("Request","Unexpected Error")
                        }
                    }
                }
            } catch (e: Exception){
                toastError(e,this@MainActivity)
            }
        }
    }
    fun toastError(e:Exception,context: Context){
        launch(Dispatchers.Main) {
            Toast.makeText(context, getString(R.string.error_connecting_server), Toast.LENGTH_LONG).show()
            Log.d("Error Occurred:", "${e.message}")
            e.printStackTrace()
        }
    }
}