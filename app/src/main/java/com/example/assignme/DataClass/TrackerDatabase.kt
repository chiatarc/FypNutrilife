package com.example.assignme.DataClass

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TrackerRecord::class], version = 2, exportSchema = false)
@TypeConverters(LocalDateConverter::class) // Keep this line for type conversion
abstract class TrackerDatabase : RoomDatabase() {
    abstract fun trackerRecordDao(): TrackerRecordDao
}
