package com.example.firebase_implementation.View.Repository

import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Email
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.View.ForgotPassword

interface authRepository {
//    fun getNotes(result: (UiStates<String>)->Unit):UiStates<List<Note>>
fun LoginUser(password: String,email: String, result: (UiStates<String>)->Unit)
fun CreateUser(password: String,email: String,user: User,result: (UiStates<String>)->Unit)
fun updateUser(user: User,result: (UiStates<String>) -> Unit)
fun ForgotPassword(email: String,result: (UiStates<String>)->Unit)
fun SigninWithGoogle(idToke:String,result: (UiStates<String>)->Unit)
fun storeSession(id:String,result: (User?)->Unit)
fun getSession(result: (User?)->Unit)
fun logout(result: ()->Unit)
fun UploadImage(imageUri: Uri,result: (String) -> Unit)

}