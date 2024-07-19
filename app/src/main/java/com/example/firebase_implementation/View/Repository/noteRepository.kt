package com.example.firebase_implementation.View.Repository

import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.UiStates

interface noteRepository {
// fun getNotes(result: (UiStates<String>)->Unit):UiStates<List<Note>>)
    fun getNotes(user: User,result: (UiStates<List<Note>>)->Unit)
    fun addNotes(note: Note,result: (UiStates<String>)->Unit)
    fun updateNote(note: Note, result: (UiStates<String>) -> Unit)
    fun deleteNote(note: Note, result: (UiStates<String>) -> Unit)

}