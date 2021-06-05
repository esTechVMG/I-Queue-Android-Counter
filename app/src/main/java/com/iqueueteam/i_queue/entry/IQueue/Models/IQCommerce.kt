package com.iqueueteam.i_queue.entry.IQueue.Models

import androidx.annotation.Keep
import java.util.*
@Keep
data class IQCommerce (
    val id:Int,
    var name:String,
    var latitude:Float,
    var longitude:Float,
    val user_id:Int,
    val image:String?,
    //var created_at:Date,
    //var updated_at:Date,
    val queue:IQQueue,
    ){
}