package com.iqueueteam.i_queue.entry

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.iqueueteam.i_queue.entry.config_storage.SharedPreferencesGson
import com.iqueueteam.i_queue.entry.databinding.ActivityEntryBinding
import com.iqueueteam.i_queue.entry.iqueue.models.IQCommerce
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import java.util.*


class EntryActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    val gson:Gson = Gson()
    private lateinit var alertDialogBuilder: MaterialAlertDialogBuilder
    lateinit var sharedPreferencesGson:SharedPreferencesGson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //late initializationm
        val wrappedContext: Context = ContextThemeWrapper(this@EntryActivity, R.style.Theme_IQueueEntry)
        alertDialogBuilder = MaterialAlertDialogBuilder(wrappedContext)
        sharedPreferencesGson = SharedPreferencesGson(baseContext)

        val commerce = sharedPreferencesGson.getObjectFromSharedPref(IQCommerce::class,getString(R.string.commerce_info_storage))
        val queueString = gson.toJson(commerce.queueInfo)
        binding.qrImageView.setImageBitmap(textToQrBitmap(queueString,2048,2048))
        val iqUser: IQUser = sharedPreferencesGson.getObjectFromSharedPref(IQUser::class,getString(R.string.user_info_storage))

        val mAwesomeValidation = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)
        mAwesomeValidation.addValidation(binding.emailInputLayoutEntry, PatternsCompat.EMAIL_ADDRESS,getString(R.string.invalid_email))

        binding.sendButtonMailEntry.setOnClickListener {

            if (mAwesomeValidation.validate()){
                launch(Dispatchers.IO){
                    iqUser.token?.let { it1 ->
                        entrymail(
                            email=binding.inputEmail.text.toString(),
                            queue_id=commerce.id,
                            context = baseContext,
                            token= it1,
                            onFailure = {
                                runOnUiThread {
                                    alertDialogBuilder
                                        .setTitle(R.string.error_title)
                                        .setMessage(getString(R.string.error_connecting_server))
                                        .setNeutralButton(R.string.button_accept,null)
                                        .setCancelable(false)
                                        .create().show()
                                    binding.inputEmail.text?.clear()
                                }
                            },
                            onSuccess = {
                                runOnUiThread {
                                    binding.inputEmail.text?.clear()
                                }
                            }
                            )
                    }
                }

            }
        }


    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}