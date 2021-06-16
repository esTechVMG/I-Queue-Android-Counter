package com.iqueueteam.i_queue.entry

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.iqueueteam.i_queue.entry.config_storage.SharedPreferencesGson
import com.iqueueteam.i_queue.entry.databinding.ActivityEntryBinding
import com.iqueueteam.i_queue.entry.iqueue.models.IQCommerce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*


class EntryActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    val gson:Gson = Gson()
    lateinit var sharedPreferencesGson:SharedPreferencesGson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferencesGson = SharedPreferencesGson(baseContext)
        val commerce = sharedPreferencesGson.getObjectFromSharedPref(IQCommerce::class,getString(R.string.commerce_info_storage))
        val queueString = gson.toJson(commerce.queueInfo)
        binding.qrImageView.setImageBitmap(textToQrBitmap(queueString,1024,1024))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}