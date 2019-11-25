package org.steffeleffe

import java.util.*


fun Calendar.getPaddedTimeOfDay(): String {
    val roundedEndMinute = (Math.floor(this.get(Calendar.MINUTE) / 15.0) * 15).toInt().toString().padStart(2, '0')
    return this.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0') + roundedEndMinute
}

fun Calendar.getPaddedDisplayTimeOfDay(): String {
    return this.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0') +
            ":" + this.get(Calendar.MINUTE).toString().padStart(2, '0')
}