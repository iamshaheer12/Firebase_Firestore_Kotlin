package com.example.firebase_implementation.View.Local_Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: NoteEntity)

    @Query("SELECT * FROM note_table") // Replace 'notes_table' with your actual table name
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Delete
     fun deleteNote(note: NoteEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
     fun updateNote(note: NoteEntity)

    @Query("DELETE FROM note_table WHERE id = :noteId")
    fun deleteNoteById(noteId: Int)
    // Uncomment and update the following methods if needed
    // @Query("SELECT * FROM note_table WHERE id = :id")
    // suspend fun getNoteById(id: String): NoteEntity?

    // @Query("DELETE FROM note_table WHERE id = :id")
    // suspend fun deleteNoteById(id: String)
}
