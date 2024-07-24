package com.example.firebase_implementation.View.Local_Data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.firebase_implementation.View.Model.Note
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
@Entity(tableName = "note_table")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) var id :Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "date") val date: Long,
   // val date: Long,
    @ColumnInfo(name = "userId") val userId: String,
//    val id: String

):Parcelable{
    override fun toString(): String {
        return "$id|$message|$userId|$title|${date}"
    }

    companion object {
        fun fromString(noteString: String): Note {
            val parts = noteString.split("|")
            return Note(
                id = parts[0],
                message = parts[1],
                userId = parts[2],
                title = parts[3],
                date = Date(parts[4].toLong())
            )
        }
    }
}