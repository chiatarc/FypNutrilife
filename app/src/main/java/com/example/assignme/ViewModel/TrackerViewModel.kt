package com.example.assignme.ViewModel

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignme.DataClass.TrackerRecord
import com.example.assignme.DataClass.TrackerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val repository: TrackerRepository,
    @ApplicationContext private val context: Context // Inject SharedPreferences through context
) : ViewModel() {

    // SharedPreferences instance for storing the calorie goal
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("tracker_prefs", Context.MODE_PRIVATE)

    private val _allEntries = MutableLiveData<List<TrackerRecord>>()
    val allEntries: LiveData<List<TrackerRecord>> get() = _allEntries

    private val _latestEntry = MutableLiveData<TrackerRecord?>()

    private val _currentWeight = MutableLiveData<Float?>(null) // Create MutableLiveData for current weight
    val currentWeight: LiveData<Float?> get() = _currentWeight // Expose it as LiveData

    private val _currentWaterIntake = MutableLiveData<Int>(0)
    val currentWaterIntake: LiveData<Int> = _currentWaterIntake

    private val _weightHistory = MutableLiveData<List<TrackerRecord>>(emptyList())
    val weightHistory: LiveData<List<TrackerRecord>> = _weightHistory

    private val _calorieHistory = MutableLiveData<List<TrackerRecord>>()
    val calorieHistory: LiveData<List<TrackerRecord>> get() = _calorieHistory

    private val _calorieGoal = MutableLiveData<Int?>()
    val calorieGoal: LiveData<Int?> get() = _calorieGoal

    init {
        fetchAllEntries()
        loadCalorieGoal() // Load calorie goal from SharedPreferences
    }

    // Load the calorie goal from SharedPreferences
    private fun loadCalorieGoal() {
        val savedGoal = sharedPreferences.getInt("calorie_goal", -1)
        if (savedGoal != -1) {
            _calorieGoal.value = savedGoal // Set the calorie goal LiveData if it was previously saved
        } else {
            _calorieGoal.value = null // No goal was set
        }
    }

    // Save the calorie goal to SharedPreferences
    private fun saveCalorieGoal(goal: Int) {
        sharedPreferences.edit().putInt("calorie_goal", goal).apply()
    }

    // Function to set the calorie goal
    fun setCalorieGoal(goal: Int) {
        _calorieGoal.value = goal
        saveCalorieGoal(goal) // Save the goal to SharedPreferences
    }

    private fun fetchAllEntries() {
        viewModelScope.launch {
            try {
                val entries = repository.getAllEntries()

                // Post all entries and latest entry
                _allEntries.postValue(entries)
                _latestEntry.postValue(entries.firstOrNull())

                // Update the weight and calorie histories
                updateHistories(entries)

            } catch (e: Exception) {
                Log.e("TrackerViewModel", "Error fetching entries: ${e.message}", e)
            }
        }
    }

    private fun updateHistories(entries: List<TrackerRecord>) {
        // Update weight history: Only include records where weight is greater than 0
        val weightHistory = entries.filter { it.weight > 0 }
        _weightHistory.postValue(weightHistory)

        // Update calorie history: Only include records where calories intake is greater than 0
        val calorieHistory = entries.filter { it.caloriesIntake > 0 }
        _calorieHistory.postValue(calorieHistory)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateWeight(date: LocalDate, weight: Float) {
        viewModelScope.launch {
            val record = repository.getEntryByDate(date)
            if (record != null) {
                record.weight = weight
                repository.insertOrUpdate(record)
                _currentWeight.value = weight // Update current weight
                fetchAllEntries() // Ensure weightHistory and other data are updated
            } else {
                Log.e("TrackerViewModel", "No record found for date: $date")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addWaterIntake(date: LocalDate) {
        viewModelScope.launch {
            val record = repository.getEntryByDate(date)
            if (record != null) {
                record.waterIntake += 1
                repository.insertOrUpdate(record)
                _currentWaterIntake.postValue(record.waterIntake) // Update LiveData immediately
                fetchAllEntries() // Update histories after adding water intake
            } else {
                val newRecord = TrackerRecord(date, waterIntake = 1)
                repository.insertOrUpdate(newRecord)
                _currentWaterIntake.postValue(1) // Set to 1 as it's a new record
                fetchAllEntries() // Update histories after adding new record
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addCalories(date: LocalDate, calories: Float) {
        viewModelScope.launch {
            val record = repository.getEntryByDate(date)
            if (record != null) {
                record.caloriesIntake += calories
                repository.insertOrUpdate(record)
                fetchAllEntries() // Update histories after adding calories
            } else {
                repository.insertOrUpdate(TrackerRecord(date, caloriesIntake = calories))
                fetchAllEntries() // Update histories after adding new record
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWaterIntakeForDate(date: LocalDate) {
        viewModelScope.launch {
            val record = repository.getEntryByDate(date)
            if (record != null) {
                _currentWaterIntake.postValue(record.waterIntake)
            } else {
                _currentWaterIntake.postValue(0) // Default to 0 if no record exists for today
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentWeight() {
        viewModelScope.launch {
            val today = LocalDate.now() // Get today's date
            val record = repository.getEntryByDate(today) // Fetch the record for today
            _currentWeight.value = record?.weight // Update current weight LiveData
        }
    }

}

