package com.iqueueteam.i_queue.entry

import android.content.SharedPreferences
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


class EntryActivity : AppCompatActivity() {
    val gson:Gson = Gson()
    lateinit var sharedPreferencesGson:SharedPreferencesGson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferencesGson = SharedPreferencesGson(this)
        val commerce = sharedPreferencesGson.getObjectFromSharedPref(IQCommerce::class,getString(R.string.commerce_info_storage))
        val queueString = gson.toJson(commerce.queueInfo)
        binding.qrImageView.setImageBitmap(textToImage(queueString,1024,1024))
    }
    @Throws(WriterException::class, NullPointerException::class)
    private fun textToImage(text: String, width: Int, height: Int): Bitmap? {
        val bitMatrix: BitMatrix
        bitMatrix = try {
            MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,
                width, height, null)
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        val colorWhite = -0x1
        val colorBlack = -0x1000000
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) colorBlack else colorWhite
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }
}