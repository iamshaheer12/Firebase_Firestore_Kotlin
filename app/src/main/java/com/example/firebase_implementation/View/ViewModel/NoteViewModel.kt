package com.example.firebase_implementation.View.ViewModel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Repository.noteRepository
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.UiStates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: noteRepository,
   // private val connectivityManager: ConnectivityManager // Inject ConnectivityManager
) : ViewModel() {

    private val _notes = MutableLiveData<UiStates<List<Note>>>()
    val notes: LiveData<UiStates<List<Note>>> get() = _notes

    private val _addNote = MutableLiveData<UiStates<String>>()
    val addNote: LiveData<UiStates<String>> get() = _addNote

    private val _updateNote = MutableLiveData<UiStates<String>>()
    val updateNote: LiveData<UiStates<String>> get() = _updateNote

    private val _deleteNote = MutableLiveData<UiStates<String>>()
    val deleteNote: LiveData<UiStates<String>> get() = _deleteNote

    // Function to get notes from the repository
    fun getNotes(user: User) {
        _notes.value = UiStates.Loading
        repository.getNotes(user) {
            _notes.value = it
        }
    }

    // Function to add a new note
    fun addNote(note: Note) {
        _addNote.value = UiStates.Loading
        repository.addNotes(note) { _addNote.value = it }
    }

    // Function to update an existing note
    fun updateNote(note: Note) {
        _updateNote.value = UiStates.Loading
        repository.updateNote(note) { _updateNote.value = it }
    }

    // Function to delete a note
    fun deleteNote(note: Note) {
        _deleteNote.value = UiStates.Loading
        repository.deleteNote(note) { _deleteNote.value = it }
    }

    // Function to check if network is available
//    fun isNetworkAvailable(): Boolean {
//        return connectivityManager.isNetworkAvailable.value ?: false
//    }
//
//    // Function to register connection observer
//    fun registerConnectionObserver(lifecycleOwner: LifecycleOwner) {
//        connectivityManager.registerConnectionObserver(lifecycleOwner)
//    }
//
//    // Function to unregister connection observer
//    fun unregisterConnectionObserver(lifecycleOwner: LifecycleOwner) {
//        connectivityManager.unregisterConnectionObserver(lifecycleOwner)
//    }
}
