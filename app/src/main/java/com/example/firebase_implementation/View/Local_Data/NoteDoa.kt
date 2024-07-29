package com.example.firebase_implementation.View.Local_Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.firebase_implementation.View.Model.DeletedNote
import com.example.firebase_implementation.View.Model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotes(notes: List<Note>)


    @Query("SELECT * FROM note_table where userId = :userId") // Replace 'notes_table' with your actual table name
    fun getAllNotes(userId: String): Flow<List<Note>>


    @Query("SELECT * FROM note_table where synced = 0 and userId = :userId") // Replace 'notes_table' with your actual table name
    fun getUnsyncedNotes(userId: String): List<Note>

    @Delete
    fun deleteNote(note: Note)

    @Query("DELETE FROM note_table where userId = :userId ")
    fun clearNotes(userId: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateNote(note: Note)

    @Query("DELETE FROM note_table WHERE id = :noteId")
    fun deleteNoteById(noteId: String)



    // Uncomment and update the following methods if needed
    // @Query("SELECT * FROM note_table WHERE id = :id")
    // suspend fun getNoteById(id: String): NoteEntity?

    // @Query("DELETE FROM note_table WHERE id = :id")
    // suspend fun deleteNoteById(id: String)
}
