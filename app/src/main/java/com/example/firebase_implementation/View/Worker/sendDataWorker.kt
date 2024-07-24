package com.example.firebase_implementation.View.Workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.firebase_implementation.View.Di.appModuel
import com.example.firebase_implementation.View.Local_Data.LocalDatabase
import com.example.firebase_implementation.View.Local_Data.NoteEntity
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Utils.FireStoreTables
import com.example.firebase_implementation.View.Utils.TypeConverter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UploadNotesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val notesList = inputData.getStringArray("notes_list") ?: return Result.failure()
        val db = FirebaseFirestore.getInstance()
        val noteDao = LocalDatabase.getDatabase(applicationContext).noteDao()
        val gson = appModuel.provideGson()

        return withContext(Dispatchers.IO) {
            try {
                notesList.forEach { noteJson ->
                    val noteEntity = gson.fromJson(noteJson, NoteEntity::class.java)
                    val note = Note(
                        id = "",
                        title = noteEntity.title,
                        message = noteEntity.message,
                        date = TypeConverter.toDate(noteEntity.date),
                        userId = noteEntity.userId
                    )

                    val document = db.collection(FireStoreTables.NOTE).document()
                    note.id = document.id

                    document.set(note).addOnFailureListener {
                        Log.e("failureof123", "doWork: ${it.message}")
                        Result.retry()
                    }.await()

                    noteDao.deleteNoteById(noteEntity.id.toInt())
                    Log.d("successof123", "$noteEntity")
                }
                Result.success()
            } catch (e: Exception) {
                Log.e("UploadNotesWorker", "Error uploading notes: ${e.message}")
                Result.failure()
            }
        }
    }
}
