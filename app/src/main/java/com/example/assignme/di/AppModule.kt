package com.example.assignme.di

import android.content.Context
import com.example.assignme.DataClass.TrackerDatabase
import com.example.assignme.DataClass.TrackerRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.room.Room
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrackerDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TrackerDatabase::class.java,
            "tracker_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTrackerRecordDao(database: TrackerDatabase): TrackerRecordDao {
        return database.trackerRecordDao()
    }
}
