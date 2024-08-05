package com.example.firebase_implementation.View.Local_Data


import MIGRATION_1_2
import MIGRATION_2_3

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.firebase_implementation.View.Model.DeletedNote
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Utils.TypeConverter
@Database(
    entities = [Note::class, DeletedNote::class],
    version = 3,
    exportSchema = true
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun deletedNoteDao(): DeletedNoteDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

