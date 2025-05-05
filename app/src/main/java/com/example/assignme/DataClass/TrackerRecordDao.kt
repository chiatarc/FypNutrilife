package com.example.assignme.DataClass

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalDate

@Dao
interface TrackerRecordDao {

    // Inserts a TrackerRecord, replacing any existing record with the same primary key
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: TrackerRecord)

    // Retrieves all TrackerRecords, ordered by date in ascending order
    @Query("SELECT * FROM tracker ORDER BY date ASC")
    suspend fun getAllEntries(): List<TrackerRecord>

    // Retrieves a TrackerRecord for a specific date
    @Query("SELECT * FROM tracker WHERE date = :date")
    suspend fun getEntryByDate(date: LocalDate): TrackerRecord?

    // New function to retrieve weight for a specific date
    @Query("SELECT weight FROM tracker WHERE date = :date LIMIT 1")
    suspend fun getWeightByDate(date: LocalDate): Float?

    // New function to retrieve calories intake for a specific date
    @Query("SELECT caloriesIntake FROM tracker WHERE date = :date LIMIT 1")
    suspend fun getCaloriesByDate(date: LocalDate): Float?

    @Query("SELECT * FROM tracker WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getEntriesForDateRange(startDate: String, endDate: String): List<TrackerRecord>
}
