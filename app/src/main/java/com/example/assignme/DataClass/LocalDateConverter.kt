package com.example.assignme.DataClass

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateConverter {

    // Formatter for LocalDate, API 26+
    @RequiresApi(Build.VERSION_CODES.O)

    // Convert LocalDate to String
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        } else {
            // Handle older versions if needed or return null
            null
        }
    }

    // Convert String to LocalDate
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && dateString != null) {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        } else {
            // Handle older versions if needed or return null
            null
        }
    }
}


