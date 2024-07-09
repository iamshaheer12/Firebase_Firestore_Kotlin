package com.example.firebase_implementation.View.ViewModel

import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Repository.noteRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Repository.authRepository
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.View.ForgotPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: authRepository
):ViewModel() {
   private val _create  = MutableLiveData<UiStates<String>>()
   val create :LiveData<UiStates<String>>
       get() = _create


    private val _login  = MutableLiveData<UiStates<String>>()
    val login :LiveData<UiStates<String>>
        get() = _login


    fun createUser(
        email:String,
        password: String,
        user: User
    ){

        _create.value = UiStates.Loading
        repository.CreateUser(email,password,user) {
            _create.value = it
        }

    }
    fun loginUser(email: String,password: String){
        _login.value = UiStates.Loading
        repository.LoginUser(email = email, password = password){
            _login.value = it
        }

    }



}