package com.example.firebase_implementation.View.Local_Data

import android.content.Context
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.firebase_implementation.View.Local_Data.LocalDatabase
import com.example.firebase_implementation.View.Local_Data.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocalDatabase {
        return databaseBuilder(
            context,
            LocalDatabase::class.java,
            "app_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Your custom logic here
                }
            })
            .build()
    }

    @Provides
    fun provideNoteDao(database: LocalDatabase): NoteDao {
        return database.noteDao()
    }
}
