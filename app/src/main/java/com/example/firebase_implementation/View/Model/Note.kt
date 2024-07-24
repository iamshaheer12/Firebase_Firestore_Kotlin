package com.example.firebase_implementation.View.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Note(
    var id: String = "",
    val message: String = "",
    val userId: String = "",
    val title: String = "",
    @ServerTimestamp
    val date: Date = Date()
) : Parcelable {
    override fun toString(): String {
        return "$id|$message|$userId|$title|${date.time}"
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
