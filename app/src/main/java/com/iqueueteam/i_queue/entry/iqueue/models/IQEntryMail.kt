package com.iqueueteam.i_queue.entry.iqueue.models

import androidx.annotation.Keep

@Keep
data class IQEntryMail (
    val queue_id:Int,
    var email:String,

    //var updated_at:Date,
    //var created_at:Date,
    ){
    enum class Role{USER,ADMIN}
}