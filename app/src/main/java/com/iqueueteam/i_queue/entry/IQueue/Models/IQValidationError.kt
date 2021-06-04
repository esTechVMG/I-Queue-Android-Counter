package com.iqueueteam.i_queue.entry.IQueue.Models

import androidx.annotation.Keep

@Keep
data class IQValidationError (
        val name:Array<String>?,
        val email:Array<String>?,
        val password:Array<String>?,
        ){
}