package com.example.firebase_implementation.View.Model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey
    @ColumnInfo(name = "id") var id: String = "",
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "message") var message: String = "",
    @ColumnInfo(name = "date") var date: Long = 0,
    @ColumnInfo(name = "userId") var userId: String = "",
    @ColumnInfo(name = "synced") var synced: Boolean = false // New attribute
) : Parcelable



