package com.iqueueteam.i_queue.entry.iqueue.models

import androidx.annotation.Keep

@Keep
data class IQCommerce (
    val id:Int,
    var name:String,
    var latitude:Float,
    var longitude:Float,
    val user_id:Int,
    val address:String,
    val image:String,
    //var created_at:Date,
    //var updated_at:Date,
    val queueInfo:IQQueue,
    ){
}