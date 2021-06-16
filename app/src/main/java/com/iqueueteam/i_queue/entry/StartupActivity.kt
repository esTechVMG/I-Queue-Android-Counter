package com.iqueueteam.i_queue.entry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.iqueueteam.i_queue.entry.config_storage.SharedPreferencesGson
import com.iqueueteam.i_queue.entry.iqueue.models.IQCommerce
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import kotlin.reflect.KClass

class StartupActivity : AppCompatActivity() {
    lateinit var sharedPreferencesGson:SharedPreferencesGson

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(getString(R.string.app_name), "Build Type: ${BuildConfig.BUILD_TYPE}")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
    }


    override fun onResume() {
        super.onResume()
        sharedPreferencesGson = SharedPreferencesGson(baseContext)
        try {
            val iqUser: IQUser = sharedPreferencesGson.getObjectFromSharedPref(IQUser::class,getString(R.string.user_info_storage))
            val iqCommerce: IQCommerce = sharedPreferencesGson.getObjectFromSharedPref(IQCommerce::class,getString(R.string.commerce_info_storage))
            //Go to Entry Activity
            intent = Intent(baseContext, EntryActivity::class.java)
            Log.i(getString(R.string.app_name),"Login Credentials retrieved. Trying to get commerce and queue info from server")

        }catch (e:Exception){
            Log.w(getString(R.string.app_name),"Could not retrieve user data")
            //Go to Login Activity
            intent = Intent(baseContext, LoginActivity::class.java)
        }
        startActivity(intent)
    }
}