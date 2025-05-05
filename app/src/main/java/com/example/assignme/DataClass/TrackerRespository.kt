package com.example.assignme.DataClass

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackerRepository @Inject constructor(private val dao: TrackerRecordDao) {

    // Function to insert or update a TrackerRecord
    suspend fun insertOrUpdate(entry: TrackerRecord) {
        try {
            withContext(Dispatchers.IO) {
                dao.insertOrUpdate(entry)
                Log.d("TrackerRepository", "Entry inserted/updated successfully: $entry")
            }
        } catch (e: Exception) {
            Log.e("TrackerRepository", "Error inserting/updating entry: ${e.message}", e)
        }
    }

    // Function to get all TrackerRecords
    suspend fun getAllEntries(): List<TrackerRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val entries = dao.getAllEntries()
                Log.d("TrackerRepository", "Fetched all entries successfully: ${entries.size} entries")
                entries
            } catch (e: Exception) {
                Log.e("TrackerRepository", "Error fetching entries: ${e.message}", e)
                emptyList()
            }
        }
    }

    // Function to get TrackerRecord by date
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getEntryByDate(date: LocalDate): TrackerRecord? {
        return withContext(Dispatchers.IO) {
            try {
                val entry = dao.getEntryByDate(date)
                Log.d("TrackerRepository", "Fetched entry for date $date: $entry")
                entry
            } catch (e: Exception) {
                Log.e("TrackerRepository", "Error fetching entry for date $date: ${e.message}", e)
                null
            }
        }
    }
}
