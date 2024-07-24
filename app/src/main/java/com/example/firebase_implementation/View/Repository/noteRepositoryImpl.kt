package com.example.firebase_implementation.View.Repository


import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.NetworkType
import com.example.firebase_implementation.View.Di.appModuel
import com.example.firebase_implementation.View.Local_Data.NoteEntity
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.FireStoreDocumentField
import com.example.firebase_implementation.View.Utils.FireStoreTables
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Workers.UploadNotesWorker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class noteRepositoryImpl(
    var database: FirebaseFirestore,

): noteRepository {
//    override fun getNotes(result: (UiStates<List<Note>>) -> Unit): UiStates<List<Note>> {
////        val data = arrayListOf<Note>()
//////            Note(
//////                "fsfa","Note1",Date()
//////            ), Note(
//////                "fa","Note2",Date()
//////            ),
//////            Note(
//////                "fs","Note4",Date()
//////            ),
//////            Note(
//////                "ffa","Note5",Date()
//////            )
//
//
////        if (data.isNullOrEmpty()){
////            return UiStates.Failure("Data is Empty")
////
////        }
////        else{
////            return UiStates.Success(data)
////        }
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

    override fun addNotes(note: Note, result: (UiStates<String>) -> Unit) {
        val document = database.collection(FireStoreTables.NOTE).document() // Generate a new document reference
        note.id = document.id // Set the note's id to the document id

        document.set(note) // Use set() instead of add()
            .addOnSuccessListener {
                result.invoke(UiStates.Success("Note added successfully"))
            }
            .addOnFailureListener {
                result.invoke(UiStates.Failure(it.localizedMessage))
            }
    }

    override fun updateNote(note: Note, result: (UiStates<String>) -> Unit) {
        val document = database.collection(FireStoreTables.NOTE).document(note.id)
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
        database.collection(FireStoreTables.NOTE).document(note.id)
            .delete()
            .addOnSuccessListener {
                result.invoke(UiStates.Success("Note successfully deleted!"))
            }
            .addOnFailureListener { e ->
                result.invoke(UiStates.Failure(e.localizedMessage))
            }
    }

    override fun scheduleNotesUpload(context: Context, noteList: List<NoteEntity>) {
        val noteStrings = noteList.map { appModuel.provideGson().toJson(it) }.toTypedArray() // Convert NoteEntity to JSON strings

        val data = Data.Builder()
            .putStringArray("notes_list", noteStrings)
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




}