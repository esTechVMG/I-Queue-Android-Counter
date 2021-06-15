package com.iqueueteam.i_queue.entry.iqueue.models

import androidx.annotation.Keep

@Keep
data class IQQueue (
    val id:Int,
    val fixed_capacity:Int,
    val current_capacity:Int,
    val average_time:Int,
    val password_verification:String,
    //val created_at:Date,
    //val updated_at:Date,
    val commerce_id:Int,
        ){
}