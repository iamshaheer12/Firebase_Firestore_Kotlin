package com.example.firebase_implementation.View.Utils

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TypeConverter {

    @TypeConverter
    fun fromDate(date: Date): Long{
        return date.time
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    // These methods are not needed by Room but are useful for display purposes
    fun formatDate(date: Date?, pattern: String = "dd MMM yyyy, hh:mm a"): String? {
        return date?.let {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            formatter.format(it)
        }
    }

    fun parseDate(dateString: String?, pattern: String = "dd MMM yyyy, hh:mm a"): Date? {
        return dateString?.let {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            formatter.parse(it)
        }
    }
}
