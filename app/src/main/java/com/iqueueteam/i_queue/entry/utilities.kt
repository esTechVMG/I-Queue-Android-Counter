package com.iqueueteam.i_queue.entry

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.iqueueteam.i_queue.entry.config_storage.SharedPreferencesGson
import com.iqueueteam.i_queue.entry.iqueue.models.IQCommerce
import com.iqueueteam.i_queue.entry.iqueue.models.IQResponse
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import com.iqueueteam.i_queue.entry.iqueue.models.IQValidationError
import com.iqueueteam.i_queue.entry.iqueue.repository.IQueueAdapter
import com.iqueueteam.i_queue.entry.iqueue.repository.LoginUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun login(
    email:String,
    password:String,
    context: Context,
    onUserAdmin: (user: IQUser) -> Unit,
    onUserNotAdmin: () -> Unit,
    onFailure:()->Unit,
){
    withContext(Dispatchers.IO) {
        // Try catch block to handle exceptions when calling the API.
        try {
            val response = IQueueAdapter.apiClient.doLogin(LoginUser( email,password))
            val body: IQResponse<IQUser?, IQValidationError?> = IQueueAdapter.getResponse(response) ?: throw Exception(context.getString(R.string.error_connecting_server))
            when (true){
                body.code in 200..299 -> {
                    Log.d("Request","Request Code:${body.code}")
                    val user: IQUser = body.data ?: throw Exception("Failed to retrieve user from login Request")
                    if(user.role == IQUser.Role.ADMIN){
                        onUserAdmin(user)
                    }else{
                        onUserNotAdmin()
                    }
                }
                else -> {
                    onFailure()
                }
            }
        } catch (e: Exception){
            toastError(e,context)
        }
    }
}
suspend fun retrieveCommerce(
    user: IQUser,
    context: Context,
    onSuccess:(commerce:IQCommerce)->Unit,
    onFailure: () -> Unit,
){
    withContext(Dispatchers.IO) {
        IQueueAdapter.token = user.token
        // Try catch block to handle exceptions when calling the API.
        try {
            val response = IQueueAdapter.apiClient.getCommerce(user.id)
            val body: IQResponse<IQCommerce, Any?> = IQueueAdapter.getResponse(response) ?: throw Exception(context.getString(R.string.error_connecting_server))
            when (true){
                body.code in 200..299 -> {
                    onSuccess(body.data?: return@withContext)
                }
                else -> {
                    onFailure()
                }
            }
        } catch (e: Exception){
            toastError(e,context)
        }
    }
}
suspend fun toastError(e:Exception, context: Context){
    withContext(Dispatchers.Main) {
        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        Log.d(context.getString(R.string.error_unexpected_error), "Error Occurred: ${e.message}")
        e.printStackTrace()
    }
}

@Throws(WriterException::class, NullPointerException::class)
fun textToQrBitmap(text: String, width: Int, height: Int): Bitmap? {
    val bitMatrix: BitMatrix = try {
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