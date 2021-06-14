package com.iqueueteam.i_queue.entry.config_storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import java.lang.Exception
import kotlin.reflect.KClass

class SharedPreferencesGson(
    val context: Context,
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("objects",Context.MODE_PRIVATE)
) {
    private val gson:Gson = Gson();
    fun <T : Any> getObjectFromSharedPref(objectType:KClass<T>,key:String):T{
        val stringToParse: String? = sharedPreferences.getString(key,"")
        val objectToReturn = gson.fromJson(stringToParse,objectType.java)
        return objectToReturn.let {
            return@let it
        } ?: kotlin.run {
            throw Exception("Could Not Parse Object")
        }
    }
    fun <T> setObjectToSharedPref(objectToParse:T, key:String){
        val stringToStore:String = gson.toJson(objectToParse);
        sharedPreferences.edit {
            this.putString(key,stringToStore)
            apply()
        }
    }
}