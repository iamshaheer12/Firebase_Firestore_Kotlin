package com.example.firebase_implementation.View.Workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.firebase_implementation.View.Local_Data.LocalDatabase
import com.example.firebase_implementation.View.Model.DeletedNote
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.FireStoreTables
import com.example.firebase_implementation.View.Utils.SharedPref
import com.example.firebase_implementation.View.di.appModule
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DeleteNotesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()
        val noteDao = LocalDatabase.getDatabase(applicationContext).deletedNoteDao()
        val user: User = appModule.provideGson().fromJson(
            appModule.provideSharedPref(applicationContext).getString(SharedPref.userSession, null), User::class.java
        )

        return withContext(Dispatchers.IO) {
            try {
                // Fetch notes marked for deletion from local database
                val notesToDelete = noteDao.getAllDeletedNotes(userId = user.id)

                // Check if there are notes to delete
                if (notesToDelete.isEmpty()) {
                    Log.d("DeleteNotesWorker", "No notes to delete found")
                    return@withContext Result.success()
                }

                // Iterate over notes to delete and remove them from Firestore
                notesToDelete.forEach { note ->
                    val document = db.collection(FireStoreTables.NOTE).document(note.id)

                    document.delete().addOnFailureListener { e ->
                        Log.e("DeleteNotesWorker", "Failed to delete note ${note.id}: ${e.message}")
                        Result.retry()
                    }.await()

                    // Delete the note from the local database
                    noteDao.deleteDeletedNoteById(note.id)
                    Log.d("DeleteNotesWorker", "Successfully deleted note ${note.id}")
                }

                Result.success()
            } catch (e: Exception) {
                Log.e("DeleteNotesWorker", "Error deleting notes: ${e.message}")
                Result.failure()
            }
        }
    }
}
