package com.iqueueteam.i_queue.entry.iqueue.models

import androidx.annotation.Keep

@Keep
data class IQValidationError (
        val name:Array<String>?,
        val email:Array<String>?,
        val password:Array<String>?,
        ){
}