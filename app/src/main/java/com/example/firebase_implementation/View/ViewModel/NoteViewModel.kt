package com.example.firebase_implementation.View.ViewModel

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebase_implementation.View.Local_Data.NoteEntity
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Repository.noteRepository
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Repository.RoomNoteRepository
import com.example.firebase_implementation.View.Utils.NetworkUtil
import com.example.firebase_implementation.View.Utils.UiStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: noteRepository,
   // private val noteDao: NoteDao,
    private val localRepository: RoomNoteRepository
   // private val connectivityManager: ConnectivityManager // Inject ConnectivityManager
) : ViewModel() {


    var recyclerViewState: Parcelable? = null

    // Firebase Data Variables
    private val _notes = MutableLiveData<UiStates<List<Note>>>()
    val notes: LiveData<UiStates<List<Note>>> get() = _notes

    private val _addNote = MutableLiveData<UiStates<String>>()
    val addNote: LiveData<UiStates<String>> get() = _addNote

    private val _updateNote = MutableLiveData<UiStates<String>>()
    val updateNote: LiveData<UiStates<String>> get() = _updateNote

    private val _deleteNote = MutableLiveData<UiStates<String>>()
    val deleteNote: LiveData<UiStates<String>> get() = _deleteNote


    //Room Data Variables
    private val _localNotes = MutableStateFlow<UiStates<List<NoteEntity>>>(UiStates.Loading)
    val localNotes: LiveData<UiStates<List<NoteEntity>>> get() = _localNotes.asLiveData()

//    private val _localNotes = MutableLiveData<UiStates<List<NoteEntity>>>()
//    val localNotes: LiveData<UiStates<List<NoteEntity>>> get() = _localNotes
//    private val _localNotes = MutableStateFlow<UiStates<List<NoteEntity>>>(UiStates.Loading)
//    val localNotes: StateFlow<UiStates<List<NoteEntity>>> get() = _localNotes

    private val _updateLocalNote = MutableLiveData<UiStates<String>>()
    val updateLocalNote: LiveData<UiStates<String>> get() = _updateLocalNote

    private val _addLocalNote = MutableLiveData<UiStates<String>>()
    val addLocalNote: LiveData<UiStates<String>> get() = _addLocalNote

    private val _deleteLocalNote = MutableLiveData<UiStates<String>>()
    val deleteLocalNote: LiveData<UiStates<String>> get() = _deleteLocalNote



    //Firebase Data Functions

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

    // Room Data Functions
    // get Local notes

    // Function to get local notes
    fun getLocalNotes() {
        viewModelScope.launch {
            localRepository.getLocalNotes().collect{ uiState ->
                _localNotes.value = uiState
            }
        }
    }

    // Function to add local note
    fun addLocalNote(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _addLocalNote.postValue(UiStates.Loading)
        localRepository.addLocalNote(note){
            _addLocalNote.postValue(it)
               // _addLocalNote.value = it

            }
        }
    }

    // Function to update local note
    fun updateLocalNote(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {

          _updateLocalNote.postValue(UiStates.Loading)
      localRepository.updateLocalNote(note){
          _updateLocalNote.postValue(it)
//          _updateLocalNote.value = it
      }
        }
    }

    // Function to delete local note
    fun deleteLocalNote(note: NoteEntity) {
        viewModelScope.launch {
          //  _deleteLocalNote.value = UiStates.Loading
           localRepository.deleteLocalNote(note){
               _deleteLocalNote.value = it
           }
        }
    }

    fun uploadNotes(context: Context, noteList: List<NoteEntity>) {
        repository.scheduleNotesUpload(context =context,noteList )
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        return NetworkUtil.isNetworkAvailable(context = context)
    }

    fun fetchNotes(user: User,context: Context) {
        if (isNetworkAvailable(context)) {
            getNotes(user = user)
//            _notes.value = UiStates.Loading
//            repository.getNotes(user) {
//                _notes.value = it
            //}
        }
        else { getLocalNotes()
//            viewModelScope.launch {
//                localRepository.getLocalNotes().collect { uiState ->
//                    _localNotes.value = uiState
//                }
//            }
        }
    }
//    fun createNote(){}


    // ConnectivityManager Functions
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
