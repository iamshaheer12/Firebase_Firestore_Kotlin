package com.example.firebase_implementation.View.Repository

import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Utils.FireStoreTables
import com.example.firebase_implementation.View.Utils.UiStates
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.Date

class noteRepositoryImpl(
    var database: FirebaseFirestore
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
    override fun getNotes(result: (UiStates<List<Note>>) -> Unit) {
        database.collection(FireStoreTables.NOTE)
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
                result.invoke(UiStates.Failure(it.localizedMessage))

            }

    }

    override fun addNotes(note: Note, result: (UiStates<String>) -> Unit) {
      database.collection(FireStoreTables.NOTE)
          .add(note)
          .addOnSuccessListener {
              result.invoke(UiStates.Success(it.id))
          }
          .addOnFailureListener{
              result.invoke(UiStates.Failure(it.localizedMessage))

          }
    }

}