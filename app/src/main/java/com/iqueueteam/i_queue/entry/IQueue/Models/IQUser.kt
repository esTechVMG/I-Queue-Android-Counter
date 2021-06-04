package com.iqueueteam.i_queue.entry.IQueue.Models

import androidx.annotation.Keep
import java.util.*

@Keep
data class IQUser (
    val id:Int,
    var name:String,
    var email:String,
    var token:String? = null,
    var role:Role,
    //var updated_at:Date,
    //var created_at:Date,
    ){
    enum class Role{USER,ADMIN}
}