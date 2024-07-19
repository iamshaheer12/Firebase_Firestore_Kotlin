package com.example.firebase_implementation.View.Model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
 data class Note(
   var id : String = "",
   val message :String = "",
   val userId:String ="",
   val title :String = "",
   @ServerTimestamp
    val date: Date = Date(),
    ):Parcelable


