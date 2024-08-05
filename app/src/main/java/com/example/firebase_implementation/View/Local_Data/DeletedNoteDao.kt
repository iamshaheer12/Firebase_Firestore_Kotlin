package com.example.firebase_implementation.View.Local_Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.firebase_implementation.View.Model.DeletedNote

@Dao
interface DeletedNoteDao {

    //Delete Table queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDeletedNote(deletedNotes: DeletedNote)

    @Query("SELECT * FROM deleted_notes where userId = :userId")
    fun getAllDeletedNotes(userId: String): List<DeletedNote>


    @Query("DELETE FROM deleted_notes WHERE id = :deletedNoteId")
    fun deleteDeletedNoteById(deletedNoteId: String)

}