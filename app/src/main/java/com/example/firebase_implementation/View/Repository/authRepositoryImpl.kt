package com.example.firebase_implementation.View.Repository

import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.FireStoreTables
import com.example.firebase_implementation.View.Utils.SharedPref
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.View.ForgotPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import java.util.Date

class authRepositoryImpl(
    var database: FirebaseFirestore,
    var auth:FirebaseAuth,
    var appPrefrences:SharedPreferences,
    val gson :Gson,
    var firebaseSorage:FirebaseStorage,
): authRepository {


    override fun LoginUser(password: String,email: String ,result: (UiStates<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{action ->
                if(action.isSuccessful){
                    storeSession(id = action.result.user?.uid?:""){
                        if (it == null){
                            result.invoke(UiStates.Failure("Logged In Successfully but Session is not stored "))


                        }
                        else{
                            result.invoke(UiStates.Success("Logged In Successfully"))

                        }
                    }
                }
            }
            .addOnFailureListener{
                result.invoke(UiStates.Failure("Failed to log In check email and password "))
            }
    }

    override fun CreateUser(email:String,password: String,user: User, result: (UiStates<String>) -> Unit) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    user.id = it.result.user?.uid?:""

                    updateUser(user){state ->
                        when(state){
                         is UiStates.Success ->{


                                 storeSession(id = it.result.user?.uid?:""){
                                     if (user == null){
                                         result.invoke(UiStates.Failure("User Created successfully but session is not saved"))
                                     }
                                     else{
                                         result.invoke(UiStates.Success("User Created Successfully"))
                                     }


                             }


                         }
                         is UiStates.Failure ->{
                             result.invoke(UiStates.Failure(state.error))
                         }

                            else -> {
                                result.invoke(UiStates.Failure("Authentication failed check email and password"))


                            }
                        }

                    }


                }
                else{
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiStates.Failure("Authentication failed, Password should be at least 6 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiStates.Failure("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiStates.Failure("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(UiStates.Failure(e.message.toString()))
                    }
                }
            }
            .addOnFailureListener{
                result.invoke(UiStates.Failure(it.localizedMessage))
            }
    }

    override fun updateUser(user: User, result: (UiStates<String>) -> Unit) {
        val document = database.collection(FireStoreTables.USER).document(user.id)
//        user.id = document.id
        document
            .set(user)
            .addOnSuccessListener {
                appPrefrences.edit().putString(SharedPref.userSession,gson.toJson(user)).apply()
                result.invoke(UiStates.Success("User Added Successfully"))
            }
            .addOnFailureListener{
                result.invoke(UiStates.Failure(it.localizedMessage))

            }
    }

    override fun ForgotPassword(email: String, result: (UiStates<String>) -> Unit) {
     auth.sendPasswordResetEmail(email)
         .addOnCompleteListener {
             if (it.isSuccessful){
                 result.invoke(UiStates.Success("Email sentSuccessfully"))
   }

         }
         .addOnFailureListener{
             result.invoke(UiStates.Failure("Failed due to invalid Email"))


         }

    }

    override fun SigninWithGoogle(idToken: String, result: (UiStates<String>) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    var user = User(
                        id = firebaseUser?.uid?:"" ,
                        name = firebaseUser?.displayName?:"",
                        email = firebaseUser?.email?:"",
                        imageUrl = firebaseUser?.photoUrl.toString(),
                        password ="" ,
                    )
                    updateUser(user){state ->
                        when(state){
                            is UiStates.Success ->{
                          //      result.invoke(UiStates.Success("User Created Successfully"))
                            storeSession(id = firebaseUser?.uid?:"" ){
                                if (firebaseUser == null){
                                    result.invoke(UiStates.Failure("Logged In Successfully but session is created"))

                                }
                                      result.invoke(UiStates.Success("User Created Successfully"))

                            }
                            }
                            is UiStates.Failure ->{
                                result.invoke(UiStates.Failure(state.error))
                            }

                            else -> {
                                result.invoke(UiStates.Failure("Authentication failed check email and password"))


                            }
                        }

                    }




                    result(UiStates.Success("Sign-in successful"))
                } else {
                    result(UiStates.Failure(task.exception?.localizedMessage ?: "Unknown error"))
                }
            }
    }

    override fun storeSession(id:String, result: (User?) -> Unit){
    database.collection(FireStoreTables.USER).document(id)
        .get()
        .addOnCompleteListener {
            if (it.isSuccessful){
                val user = it.result.toObject(User::class.java)
                result.invoke(user)
                appPrefrences.edit().putString(SharedPref.userSession,gson.toJson(user)).apply()

            }
            else{
                result.invoke(null)
            }

        }
        .addOnFailureListener {
            result.invoke(null)
        }


}

    override fun getSession(result: (User?) -> Unit) {
        val usr_str = appPrefrences.getString(SharedPref.userSession,null)
        if (usr_str == null){
            result.invoke(null)
        }
        else{
            val user = gson.fromJson(usr_str,User::class.java)
            result.invoke(user)
        }
    }


    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPrefrences.edit().putString(SharedPref.userSession,null).apply()
        result.invoke()

    }

    override fun UploadImage(imageUri: Uri, result: (String) -> Unit) {
        try {
            val storageRef = firebaseSorage.reference.child("uploads/${System.currentTimeMillis()}.jpg")
            storageRef.putFile(imageUri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    result(uri.toString())
                }
            }.addOnFailureListener {
                result("Image is not Uploaded")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result("")
        }
    }


}

