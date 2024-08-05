package com.example.firebase_implementation.View.ViewModel

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebase_implementation.View.Model.DeletedNote
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Repository.noteRepository
import com.example.firebase_implementation.View.Model.User
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
//private val repository: RoomNoteRepository
    // private val connectivityManager: ConnectivityManager // Inject ConnectivityManager
) : ViewModel() {


    var recyclerViewState: Parcelable? = null

//
    private val _overrideNotesState = MutableLiveData<UiStates<String>>()
    val overrideNotesState: LiveData<UiStates<String>> get() = _overrideNotesState

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
    private val _localNotes = MutableStateFlow<UiStates<List<Note>>>(UiStates.Loading)
    val localNotes: LiveData<UiStates<List<Note>>> get() = _localNotes.asLiveData()

//    private val _localNotes = MutableLiveData<UiStates<List<Note>>>()
//    val localNotes: LiveData<UiStates<List<Note>>> get() = _localNotes
//    private val _localNotes = MutableStateFlow<UiStates<List<Note>>>(UiStates.Loading)
//    val localNotes: StateFlow<UiStates<List<Note>>> get() = _localNotes

    private val _updateLocalNote = MutableLiveData<UiStates<String>>()
    val updateLocalNote: LiveData<UiStates<String>> get() = _updateLocalNote

    private val _addLocalNote = MutableLiveData<UiStates<String>>()
    val addLocalNote: LiveData<UiStates<String>> get() = _addLocalNote

    private val _deleteLocalNote = MutableLiveData<UiStates<Note>>()
    val deleteLocalNote: LiveData<UiStates<Note>> get() = _deleteLocalNote

    // Local deleted note
    private val _addDeletedLocalNote = MutableLiveData<UiStates<String>>()
    val addDeletedLocalNote: LiveData<UiStates<String>> get() = _addDeletedLocalNote

    // Search Variables
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery


    private val _searchResults = MutableLiveData<List<Note>>()
    val searchResults: LiveData<List<Note>> get() = _searchResults
// Search Functions
  fun setSearchQuery(query: String) {
    _searchQuery.value = query
    filterNotes(query)
  }

    private fun filterNotes(query: String) {
        val allNotes = (_notes.value as? UiStates.Success)?.data ?: emptyList()
        val filteredList = allNotes.filter { item ->
            item.title.contains(query, ignoreCase = true) ||
                    item.message.contains(query, ignoreCase = true) ||
                    item.date.toString().contains(query, ignoreCase = true)
        }
        _searchResults.value = filteredList
    }

    fun setNotes(notes: List<Note>) {
        _notes.value = UiStates.Success(notes)
        filterNotes(_searchQuery.value ?: "")
    }

//    private fun filterNotes(query: String) {
//        val allNotes = (_notes.value as? UiStates.Success)?.data ?: emptyList()
//        val filteredList = allNotes.filter { item ->
//            item.title.contains(query, ignoreCase = true) ||
//                    item.message.contains(query, ignoreCase = true) ||
//                    item.date.toString().contains(query, ignoreCase = true)
//        }
//        _searchResults.value = filteredList
//    }
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
    fun getLocalNotes(user: User) {
        viewModelScope.launch {
            repository.getLocalNotes(user).collect{ uiState ->
                _localNotes.value = uiState
            }
        }
    }

    // Function to add local note
    fun addLocalNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            _addLocalNote.postValue(UiStates.Loading)
            repository.addLocalNote(note){
                _addLocalNote.postValue(it)
                // _addLocalNote.value = it

            }
        }
    }

    // Function to update local note
    fun updateLocalNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {

            _updateLocalNote.postValue(UiStates.Loading)
            repository.updateLocalNote(note){
                _updateLocalNote.postValue(it)
//          _updateLocalNote.value = it
            }
        }
    }

    // Function to delete local note
    fun deleteLocalNote(note: Note) {
        viewModelScope.launch {
            //  _deleteLocalNote.value = UiStates.Loading
            _deleteLocalNote.postValue(UiStates.Loading)
            repository.deleteLocalNote(note){
                _deleteLocalNote.postValue(it)
            }
        }
    }

    fun uploadNotes(context: Context) {
        repository.scheduleNotesUpload(context =context)
    }

    fun uploadDeletedNotes(context: Context) {
        repository.scheduleNotesDeleted(context =context)
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        return NetworkUtil.isNetworkAvailable(context = context)
    }
    fun deleteNote1(note: Note,context: Context){
        if (isNetworkAvailable(context)) {
            deleteNote(note)
        }
        else {
            deleteLocalNote(note)
        }

    }

    fun fetchNotes(user: User,context: Context) {
        if (isNetworkAvailable(context)) {
            getNotes(user = user)

            overrideNotesWithFirebaseData(user)

        }
        else { getLocalNotes(user)
//            viewModelScope.launch {
//                repository.getLocalNotes().collect { uiState ->
//                    _localNotes.value = uiState
//                }
//            }
        }
    }
    // Function to override notes with Firebase data
    fun overrideNotesWithFirebaseData(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            _overrideNotesState.postValue(UiStates.Loading)
            try {
                repository.overrideNotesWithFirebaseData(user)

                _overrideNotesState.postValue(UiStates.Success("Notes overridden successfully"))
                //value =
            } catch (e: Exception) {
                _overrideNotesState.postValue(UiStates.Failure(e.message ?: "Unknown error"))
                    //.value =
            }
        }
    }

    fun addDeletedLocalNote(note: DeletedNote) {
        viewModelScope.launch(Dispatchers.IO) {
            _addDeletedLocalNote.postValue(UiStates.Loading)
            repository.addDeletedLocalNote(note){
                _addDeletedLocalNote.postValue(it)
            }
    }}
//    fun createNote(){}
    // fun for deleted note  add into room


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