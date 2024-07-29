package com.example.firebase_implementation.View.Workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.firebase_implementation.View.Local_Data.LocalDatabase
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.FireStoreTables
import com.example.firebase_implementation.View.Utils.SharedPref
import com.example.firebase_implementation.View.di.appModule
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UploadNotesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()
        val noteDao = LocalDatabase.getDatabase(applicationContext).noteDao()
        val user: User =appModule.provideGson().fromJson(
            appModule.provideSharedPref(applicationContext).getString(SharedPref.userSession, null),User::class.java
        )

        return withContext(Dispatchers.IO) {
            try {
                // Fetch unsynced notes from local database
                var unSyncedNotes = noteDao.getUnsyncedNotes(user.id)

                // Check if there are unsynced notes
//
                if (unSyncedNotes.isEmpty()) {
                    Log.d("UploadNotesWorker", "No unsynced notes found")
                    return@withContext Result.success()
                }

                // Iterate over unsynced notes and upload them to Firestore
                unSyncedNotes.forEach { note ->
                    val note1 = Note(
                        id = note.id,
                        title = note.title,
                        message = note.message,
                        date = note.date,
                        userId = note.userId,
                        synced = true
                    )
                    val document = db.collection(FireStoreTables.NOTE).document(note.id)

                    document.set(note1).addOnFailureListener { e ->
                        Log.e("UploadNotesWorker", "Failed to upload note ${note.id}: ${e.message}")
                        Result.retry()
                    }.await()

                    // Update the note's sync status
//                    noteDao.updateNote(Note(
//                        id = note.id,
//                        title = note.title,
//                        message = note.message,
//                        date = note.date,
//                        userId = note.userId,
//                        synced = true
//                    ))

                    Log.d("UploadNotesWorker", "Successfully uploaded note ${note.id}")
                }

                Result.success()
            } catch (e: Exception) {
                Log.e("UploadNotesWorker", "Error uploading notes: ${e.message}")
                Result.failure()
            }
        }
    }
}
