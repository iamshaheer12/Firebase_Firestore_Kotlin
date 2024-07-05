package com.example.firebase_implementation.View.View

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_implementation.R
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val user:MutableMap<String,Any> = HashMap()
        user["name"] = "Muhammad Shaheer"
        user["born"] = 2003
        user["location"] = "Lahore"
        FirebaseFirestore.getInstance().collection("users")
            .add(user)
            .addOnSuccessListener { documentRefrence ->

                Log.d("TAG", "Document add with id :" + documentRefrence.id)
            }
            .addOnFailureListener{
                e ->
                Log.w("TAG", "Error adding document" + e)
            }

    }
}