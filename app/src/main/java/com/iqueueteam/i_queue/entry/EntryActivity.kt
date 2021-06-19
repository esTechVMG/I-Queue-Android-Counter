package com.iqueueteam.i_queue.entry

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.iqueueteam.i_queue.entry.config_storage.SharedPreferencesGson
import com.iqueueteam.i_queue.entry.databinding.ActivityEntryBinding
import com.iqueueteam.i_queue.entry.iqueue.models.IQCommerce
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.*


class EntryActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    val gson:Gson = Gson()
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    lateinit var sharedPreferencesGson:SharedPreferencesGson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityEntryBinding.inflate(layoutInflater)
        alertDialogBuilder = AlertDialog.Builder(baseContext)
        setContentView(binding.root)
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