package com.example.firebase_implementation.View.ViewModel

import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Repository.noteRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebase_implementation.View.Utils.UiStates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(
    val repository: noteRepository
):ViewModel() {
   private val _notes  = MutableLiveData<UiStates<List<Note>>>()
   val note :LiveData<UiStates<List<Note>>>
       get() = _notes



    private val _addNotes  = MutableLiveData<UiStates<String>>()
    val addNote :LiveData<UiStates<String>>
        get() = _addNotes


    fun getNotes(){

        _notes.value = UiStates.Loading
        repository.getNotes {
            _notes.value = it
        }

    }

    fun addNote(note: Note){
        _addNotes.value = UiStates.Loading
        repository.addNotes(note) { _addNotes.value = it }

    }

}