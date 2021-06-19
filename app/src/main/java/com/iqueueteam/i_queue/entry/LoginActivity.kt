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
        sharedPreferencesGson = SharedPreferencesGson(baseContext)
        alertDialogBuilder = AlertDialog.Builder(baseContext)

        binding.sendButton.setOnClickListener {
            if (mAwesomeValidation.validate()){
                launch(Dispatchers.IO){
                    login(
                        binding.emailInputEditText.text.toString(),
                        binding.passwordlInputEditText.text.toString(),
                        baseContext,
                        onFailure = {
                            //We don t do anything on failure
                        },
                        onUserAdmin = {
                            launch {
                                sharedPreferencesGson.setObjectToSharedPref(it,baseContext.getString(R.string.user_info_storage))
                                Log.d(baseContext.getString(R.string.app_name),"User Logged In successfully")
                                retrieveCommerce(
                                    it,
                                    baseContext,
                                    onSuccess = {commerce ->
                                        sharedPreferencesGson.setObjectToSharedPref(commerce,baseContext.getString(R.string.commerce_info_storage))
                                        finish()
                                    },
                                    onFailure = {
                                        alertDialogBuilder
                                            .setTitle(R.string.error_title)
                                            .setMessage(R.string.error_connecting_server)
                                            .setNeutralButton(R.string.button_accept,null)
                                            .setCancelable(true)
                                            .create().show()
                                    }
                                )
                            }
                        },
                        onUserNotAdmin = {
                            alertDialogBuilder
                                .setTitle(R.string.error_title)
                                .setMessage(R.string.user_not_admin)
                                .setNeutralButton(R.string.button_accept,null)
                                .setCancelable(true)
                                .create().show()
                        }
                    )
                }
            }
        }

    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
