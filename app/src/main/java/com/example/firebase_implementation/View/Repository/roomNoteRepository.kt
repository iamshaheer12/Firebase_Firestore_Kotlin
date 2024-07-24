package com.example.firebase_implementation.View.Repository

import com.example.firebase_implementation.View.Local_Data.NoteEntity
import com.example.firebase_implementation.View.Utils.UiStates
import kotlinx.coroutines.flow.Flow

interface RoomNoteRepository {
       fun getLocalNotes(): Flow<UiStates<List<NoteEntity>>>
       suspend fun addLocalNote(note: NoteEntity, result: (UiStates<String>) -> Unit)
       suspend fun updateLocalNote(note: NoteEntity, result: (UiStates<String>) -> Unit)
       suspend fun deleteLocalNote(note: NoteEntity, result: (UiStates<String>) -> Unit)
    }