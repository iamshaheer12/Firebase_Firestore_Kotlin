package com.example.firebase_implementation.View.Repository

import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.FireStoreTables
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.View.ForgotPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import java.util.Date

class authRepositoryImpl(
    var database: FirebaseFirestore,
    var auth:FirebaseAuth
): authRepository {


    override fun LoginUser(password: String,email: String ,result: (UiStates<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{action ->
                if(action.isSuccessful){
                    result.invoke(UiStates.Success("Logged In Successfully"))
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
                    updateUser(user){state ->
                        when(state){
                         is UiStates.Success ->{
                             result.invoke(UiStates.Success("User Created Successfully"))
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
        val document = database.collection(FireStoreTables.USER).document()
        user.id = document.id
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(UiStates.Success("Note has been created successfully"))
            }
            .addOnFailureListener{
                result.invoke(UiStates.Failure(it.localizedMessage))

            }
    }

    override fun ForgotPassword(user: User, result: (UiStates<String>) -> Unit) {
        TODO("Not yet implemented")
    }




}

