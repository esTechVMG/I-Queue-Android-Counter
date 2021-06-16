package com.iqueueteam.i_queue.entry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.iqueueteam.i_queue.entry.config_storage.SharedPreferencesGson
import com.iqueueteam.i_queue.entry.iqueue.models.IQUser
import kotlin.reflect.KClass

class StartupActivity : AppCompatActivity() {
    lateinit var sharedPreferencesGson:SharedPreferencesGson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
        //If for some reason we come from other activity close the app
        if (intent.getBooleanExtra("EXIT", false)) finish()

        sharedPreferencesGson = SharedPreferencesGson(this)
        var intent:Intent?
        //Try to retrieve user object from SharedPreference
        try {
            val iqUser: IQUser = sharedPreferencesGson.getObjectFromSharedPref(IQUser::class,getString(R.string.user_info_storage))
            //Go to Entry Activity
            intent = Intent(baseContext, EntryActivity::class.java)
            Log.i(getString(R.string.app_name),"Login Credentials retrieved. Trying to get commerce and queue info from server")

        }catch (e:Exception){
            Log.w(getString(R.string.app_name),"Could not retrieve user data")
            //Go to Login Activity
            intent = Intent(baseContext, LoginActivity::class.java)
        }
        startActivity(intent);
    }
}