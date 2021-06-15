package com.iqueueteam.i_queue.entry

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.iqueueteam.i_queue.entry.config_storage.SharedPreferencesGson
import com.iqueueteam.i_queue.entry.databinding.ActivityLoginBinding
import com.iqueueteam.i_queue.entry.iqueue.models.IQCommerce
import com.iqueueteam.i_queue.entry.iqueue.models.IQResponse
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import com.iqueueteam.i_queue.entry.iqueue.models.IQValidationError
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter.apiClient
import com.iqueueteam.i_queue.entry.iqueue.repository.LoginUser
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var sharedPreferencesGson:SharedPreferencesGson
    private lateinit var alertDialogBuilder:AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(getString(R.string.app_name), "Build Type: ${BuildConfig.BUILD_TYPE}")

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Validations
        val mAwesomeValidation = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)
        mAwesomeValidation.addValidation(binding.emailInputLayout,PatternsCompat.EMAIL_ADDRESS,getString(R.string.invalid_email))
        mAwesomeValidation.addValidation(binding.passwordInputLayout, { input:String ->
            return@addValidation input.length >= 4
        },getString(R.string.password_length))
        //late initialization
        sharedPreferencesGson = SharedPreferencesGson(this)
        alertDialogBuilder = AlertDialog.Builder(this)

        //Try to retrieve user object from SharedPreference
        try {
            val iqUser:IQUser = sharedPreferencesGson.getObjectFromSharedPref(IQUser::class,getString(R.string.user_info_storage))
            Log.d(getString(R.string.app_name),"Login Credentials retreived. Trying to get commerce and queue info from server")
            retrieveCommerce(iqUser)

        }catch (e:Exception){
            Log.d(getString(R.string.app_name),"Could not retrieve user data")
        }
        binding.sendButton.setOnClickListener {
            if (mAwesomeValidation.validate()){
                login(
                    binding.emailInputEditText.text.toString(),
                    binding.passwordlInputEditText.text.toString()
                )
            }
        }

    }
    private fun login(email:String,password:String){
        launch(Dispatchers.IO) {
            // Try catch block to handle exceptions when calling the API.
            try {
                val response = apiClient.doLogin(LoginUser( email,password))
                val body:IQResponse<IQUser?,IQValidationError?> = IQueueAdapter.getResponse(response) ?: throw Exception(getString(R.string.error_connecting_server))
                when (true){
                    body.code in 200..299 -> {
                        Log.d("Request","Request Code:${body.code}")
                        val user:IQUser = body.data ?: throw Exception("Failed to retrieve user from login Request")
                        if(user.role == IQUser.Role.ADMIN){
                            sharedPreferencesGson.setObjectToSharedPref(user,getString(R.string.user_info_storage))
                            Log.d(getString(R.string.app_name),"User Logged In successfully")
                            retrieveCommerce(user)
                        }else{
                            Log.d(getString(R.string.app_name),"Error: ${getString(R.string.user_not_admin)}")
                            alertDialogBuilder
                                .setTitle(R.string.error_title)
                                .setMessage(R.string.user_not_admin)
                                .setNeutralButton(R.string.button_accept,null)
                                .setCancelable(true)
                                .create().show()
                        }
                    }
                    else -> {
                        throw  Exception("Unexpected Error")
                    }
                }
            } catch (e: Exception){
                toastError(e,this@LoginActivity)
            }
        }
    }

    private fun retrieveCommerce(user:IQUser){
            launch(Dispatchers.IO) {
                IQueueAdapter.token = user.token
                // Try catch block to handle exceptions when calling the API.
                try {
                    val response = apiClient.getCommerce(user.id)
                    val body:IQResponse<IQCommerce, Any?> = IQueueAdapter.getResponse(response) ?: throw Exception(getString(R.string.error_connecting_server))
                    when (true){
                        body.code in 200..299 -> {
                            Log.d("Request","Request Code:${body.code}")
                            val commerce:IQCommerce = body.data ?: throw Exception("Failed to retrieve user from login Request")
                            sharedPreferencesGson.setObjectToSharedPref(commerce,getString(R.string.commerce_info_storage))
                            launchQrActivity()
                        }
                        else -> {
                            throw  Exception("Unexpected Error")
                        }
                    }
                } catch (e: Exception){
                    toastError(e,this@LoginActivity)
                }
            }
        }
    private fun toastError(e:Exception, context: Context){
        launch(Dispatchers.Main) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            Log.d(getString(R.string.error_unexpected_error), "Error Occurred: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun launchQrActivity(){
        val intent = Intent(baseContext, EntryActivity::class.java)
        startActivity(intent);
    }


}
