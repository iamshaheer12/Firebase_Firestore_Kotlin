package com.example.firebase_implementation.View.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "deleted_notes")
data class DeletedNote(
    @PrimaryKey val id: String,
    val userId: String
):Parcelable
