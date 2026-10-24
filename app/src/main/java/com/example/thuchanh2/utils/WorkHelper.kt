package com.example.thuchanh2.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object WorkHelper {
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun calculateWorkingHours(checkIn: String, checkOut: String): Double {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val inTime = sdf.parse(checkIn)
            val outTime = sdf.parse(checkOut)
            if (inTime != null && outTime != null) {
                val diffInMillis = outTime.time - inTime.time
                if (diffInMillis < 0) return -1.0
                val hours = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) / 60.0
                String.format("%.2f", hours).toDouble()
            } else {
                -1.0
            }
        } catch (e: Exception) {
            -1.0
        }
    }
}