package com.example.firebase_implementation.View.Repository

import com.example.firebase_implementation.View.Local_Data.NoteDao
import com.example.firebase_implementation.View.Local_Data.NoteEntity
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.UiStates.Success
import javax.inject.Inject

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class roomNoteRepositoryImpl @Inject constructor(    private val noteDao: NoteDao
)
 : RoomNoteRepository {

    override fun getLocalNotes(): Flow<UiStates<List<NoteEntity>>> = flow {
        emit(UiStates.Loading)
        try {
            noteDao.getAllNotes().collect { notes ->
                emit(UiStates.Success(notes))
            }
        } catch (e: Exception) {
            emit(UiStates.Failure(e.message ?: "Unknown error"))
        }
    }


//    override suspend fun getLocalNotes(result: (UiStates<Flow<List<NoteEntity>>>) -> Unit) {
//        return withContext(Dispatchers.IO) {
//            try {
//                UiStates.Success(noteDao.getAllNotes())
//            } catch (e: Exception) {
//                UiStates.Failure(e.localizedMessage ?: "Unknown error")
//            }
//        }
//    }

    override suspend fun addLocalNote(note: NoteEntity, result: (UiStates<String>) -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                noteDao.insertNote(note)
               result.invoke(UiStates.Success("Note added successfully"))
                Success("Note added successfully")
            } catch (e: Exception) {
                result.invoke(UiStates.Failure(e.localizedMessage ?:"Unknown error"))

                UiStates.Failure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    override suspend fun deleteLocalNote(note: NoteEntity, result: (UiStates<String>) -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                noteDao.deleteNote(note)
                Success("Note successfully deleted!")
            } catch (e: Exception) {
                UiStates.Failure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    override suspend fun updateLocalNote(note: NoteEntity, result: (UiStates<String>) -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                noteDao.updateNote(note)
                result.invoke( UiStates.Success("Note updated successfully"))

            } catch (e: Exception) {
                result.invoke(  UiStates.Failure(e.localizedMessage ?: "Unknown error"))


            }
        }
    }
}
