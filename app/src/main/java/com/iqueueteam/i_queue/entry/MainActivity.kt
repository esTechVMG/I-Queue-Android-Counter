package com.iqueueteam.i_queue.entry

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iqueueteam.i_queue.entry.IQueue.Models.IQResponse
import com.iqueueteam.i_queue.entry.IQueue.Models.IQUser
import com.iqueueteam.i_queue.entry.IQueue.Models.IQValidationError
import com.iqueueteam.i_queue.entry.IQueue.Repository.IQueueAdapter
import com.iqueueteam.i_queue.entry.IQueue.Repository.IQueueAdapter.apiClient
import com.iqueueteam.i_queue.entry.IQueue.Repository.LoginUser
import kotlinx.coroutines.*
import retrofit2.Response


class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var sendButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("I-Queue", "Build Type: ${BuildConfig.BUILD_TYPE}")
        sendButton = findViewById(R.id.send_button)
        sendButton.setOnClickListener {

        }
    }
    fun re(){
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
                launch(Dispatchers.Main) {
                    // Show API error.
                    Toast.makeText(this@MainActivity,
                        "Error Occurred: ${e.message}",
                        Toast.LENGTH_LONG).show()
                    Log.d("Error Occurred:", "${e.message}")
                }
            }
        }
    }
}