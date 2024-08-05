package com.example.firebase_implementation.View.Repository


import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.NetworkType
import com.example.firebase_implementation.View.Local_Data.DeletedNoteDao
import com.example.firebase_implementation.View.di.appModule
import com.example.firebase_implementation.View.Local_Data.NoteDao
import com.example.firebase_implementation.View.Model.DeletedNote
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.FireStoreDocumentField
import com.example.firebase_implementation.View.Utils.FireStoreTables
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.UiStates.Success
import com.example.firebase_implementation.View.Workers.DeleteNotesWorker
import com.example.firebase_implementation.View.Workers.UploadNotesWorker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class noteRepositoryImpl(
    var database: FirebaseFirestore,
    private val noteDao: NoteDao,
    private val deleteNoteDao: DeletedNoteDao

): noteRepository {
//      }
//    }
    override fun getNotes(user: User,result: (UiStates<List<Note>>) -> Unit) {
        database.collection(FireStoreTables.NOTE)
            .whereEqualTo(FireStoreDocumentField.USER_ID,user.id)
        //.orderBy(FireStoreDocumentField.DATE, Query.Direction.DESCENDING)
        .get()

            .addOnSuccessListener {
                val notes = ArrayList<Note>()
                for (document in it){
                    val note = document.toObject<Note>()
                    notes.add(note)
                }
                result.invoke(UiStates.Success(notes))


            }
            .addOnFailureListener{
                Log.e("Query_error",it.localizedMessage)
                result.invoke(UiStates.Failure(it.localizedMessage))

            }

    }

    private suspend fun getFirebaseNotes(user: User): List<Note> {
        val notesList = mutableListOf<Note>()
        val firebaseState = suspendCancellableCoroutine<UiStates<List<Note>>> { continuation ->
            getNotes(user) { state ->
                continuation.resume(state) {}
            }
        }
        if (firebaseState is UiStates.Success) {
            notesList.addAll(firebaseState.data)
        }
        return notesList
    }

    override suspend fun overrideNotesWithFirebaseData(user: User) {
        val firebaseNotes = getFirebaseNotes(user)
        noteDao.clearNotes(user.id)
        noteDao.insertNotes(firebaseNotes)
    }

    override fun addNotes(note: Note, result: (UiStates<String>) -> Unit) {
        val document = database.collection(FireStoreTables.NOTE).document(note.id.toString())
        //    .whereEqualTo(FireStoreDocumentField.,note.id)
            //note.synced =true

            //.document() // Generate a new document reference
         //document.id = note.id.toString()  // Set the note's id to the document id

        document.set(note) // Use set() instead of add()
            .addOnSuccessListener {
                result.invoke(UiStates.Success("Note added successfully"))
            }
            .addOnFailureListener {
                result.invoke(UiStates.Failure(it.localizedMessage))
            }
    }

    override fun updateNote(note: Note, result: (UiStates<String>) -> Unit) {
        val document = database.collection(FireStoreTables.NOTE).document(note.id.toString())
        document
            .set(note)
            .addOnSuccessListener {
                result.invoke(
                    UiStates.Success("Note has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiStates.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun deleteNote(note: Note, result: (UiStates<String>) -> Unit) {
        database.collection(FireStoreTables.NOTE).document(note.id.toString())
            .delete()
            .addOnSuccessListener {
                result.invoke(UiStates.Success("Note successfully deleted!"))
            }
            .addOnFailureListener { e ->
                result.invoke(UiStates.Failure(e.localizedMessage))
            }
    }

    override fun scheduleNotesUpload(context: Context) {
        //val noteStrings = noteList.map { appModule.provideGson().toJson(it) }.toTypedArray() // Convert NoteEntity to JSON strings

        val data = Data.Builder()
         //   .putStringArray("notes_list", noteStrings)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = OneTimeWorkRequest.Builder(UploadNotesWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
    }
    override fun scheduleNotesDeleted(context: Context) {
        //val noteStrings = noteList.map { appModule.provideGson().toJson(it) }.toTypedArray() // Convert NoteEntity to JSON strings

        val data = Data.Builder()
            //   .putStringArray("notes_list", noteStrings)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val deleteWorkRequest = OneTimeWorkRequest.Builder(DeleteNotesWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(deleteWorkRequest)
    }
    override fun getLocalNotes(user: User): Flow<UiStates<List<Note>>> = flow {
        emit(UiStates.Loading)
        try {
            noteDao.getAllNotes(userId = user.id).collect { notes ->
                emit(UiStates.Success(notes))
            }
        } catch (e: Exception) {
            emit(UiStates.Failure(e.message ?: "Unknown error"))
        }
    }


//    override suspend fun getLocalNotes(result: (UiStates<Flow<List<Note>>>) -> Unit) {
//        return withContext(Dispatchers.IO) {
//            try {
//                UiStates.Success(noteDao.getAllNotes())
//            } catch (e: Exception) {
//                UiStates.Failure(e.localizedMessage ?: "Unknown error")
//            }
//        }
//    }

    override suspend fun addLocalNote(note: Note, result: (UiStates<String>) -> Unit) {
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

    override suspend fun deleteLocalNote(note: Note, result: (UiStates<Note>) -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                noteDao.deleteNote(note)
                result.invoke(UiStates.Success(note))
                Success("Note successfully deleted!")
            } catch (e: Exception) {
                UiStates.Failure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    override suspend fun updateLocalNote(note: Note, result: (UiStates<String>) -> Unit) {
        return withContext(Dispatchers.IO) {
            try {
                noteDao.updateNote(note)
                result.invoke( UiStates.Success("Note updated successfully"))

            } catch (e: Exception) {
                result.invoke(  UiStates.Failure(e.localizedMessage ?: "Unknown error"))


            }
        }
    }

    override suspend fun addDeletedLocalNote(
        note: DeletedNote,
        result: (UiStates<String>) -> Unit
    ) {
       return withContext(Dispatchers.IO) {
            try {
                deleteNoteDao.insertDeletedNote(note)

                result.invoke(UiStates.Success("Note added successfully"))
                Success("Note added successfully")
            } catch (e: Exception) {
                result.invoke(UiStates.Failure(e.localizedMessage ?:"Unknown error"))

                UiStates.Failure(e.localizedMessage ?: "Unknown error")
            }
        }

    }



}