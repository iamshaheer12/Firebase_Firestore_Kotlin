package com.example.firebase_implementation.View.Repository

import android.content.Context
import com.example.firebase_implementation.View.Model.DeletedNote
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.UiStates
import kotlinx.coroutines.flow.Flow

interface noteRepository {
// fun getNotes(result: (UiStates<String>)->Unit):UiStates<List<Note>>)
    fun getNotes(user: User,result: (UiStates<List<Note>>)->Unit)
    fun addNotes(note: Note,result: (UiStates<String>)->Unit)
    fun updateNote(note: Note, result: (UiStates<String>) -> Unit)
    fun deleteNote(note: Note, result: (UiStates<String>) -> Unit)
  //  fun scheduleNotesUpload(noteList: List<Note>)
    suspend fun overrideNotesWithFirebaseData(user: User)
    fun scheduleNotesUpload(context: Context)
    fun scheduleNotesDeleted(context: Context)
    fun getLocalNotes(user: User): Flow<UiStates<List<Note>>>
    suspend fun addLocalNote(note: Note, result: (UiStates<String>) -> Unit)
    suspend fun updateLocalNote(note: Note, result: (UiStates<String>) -> Unit)
    suspend fun deleteLocalNote(note: Note, result: (UiStates<Note>) -> Unit)
    suspend fun addDeletedLocalNote(note: DeletedNote, result: (UiStates<String>) -> Unit)
}