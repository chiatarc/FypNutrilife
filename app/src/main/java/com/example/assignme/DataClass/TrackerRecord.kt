package com.example.assignme.DataClass

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

@Entity(tableName = "tracker")
@TypeConverters(LocalDateConverter::class)
data class TrackerRecord(
    @PrimaryKey val date: LocalDate, // Using LocalDate as primary key
    var weight: Float = 0f,          // Initialized to 0
    var waterIntake: Int = 0,        // Changed to Int and initialized to 0
    var caloriesIntake: Float = 0f    // Initialized to 0
)
