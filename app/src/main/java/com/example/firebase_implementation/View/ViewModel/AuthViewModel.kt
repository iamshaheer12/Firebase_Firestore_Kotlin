package com.example.firebase_implementation.View.ViewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Repository.authRepository
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.UiStates.*
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

    private val _forgot  = MutableLiveData<UiStates<String>>()
    val forgot :LiveData<UiStates<String>>
        get() = _forgot


    private val _signInState = MutableLiveData<UiStates<String>>()
    val signInState: LiveData<UiStates<String>> get() = _signInState


    private val _updateState = MutableLiveData<UiStates<String>>()
    val updateState: LiveData<UiStates<String>> get() = _updateState


    private val _uploadState = MutableLiveData<UiStates<String>>()
    val uploadState: LiveData<UiStates<String>> get() = _uploadState


    fun createUser(
        email:String,
        password: String,
        user: User
    ){
        _create.value = Loading
        repository.CreateUser(email,password,user) {
            _create.value = it
        }

    }
    fun loginUser(email: String,password: String){
        _login.value = Loading
        repository.LoginUser(email = email, password = password){
            _login.value = it
        }

    }
    fun forgotPassword(email: String){
        _forgot.value = Loading
        repository.ForgotPassword(email){
            _forgot.value = it
        }

    }


    fun signInWithGoogle(idToken: String) {
        _signInState.value = Loading
        repository.SigninWithGoogle(idToken) { state ->
            _signInState.value = state
        }
    }

    fun getSession(result: (User?) ->Unit){
        return repository.getSession(result)

    }
    fun updateSession(){

    }
    fun logOut(result: () -> Unit){
        return repository.logout(result)
    }
   fun updateUser(user: User){
       _updateState.value = Loading
repository.updateUser(user){
    _updateState.value = it

}
   }
    fun uploadImage(imageUri: Uri, param: (Any) -> Unit,){
        _uploadState.value = Loading
        repository.UploadImage(imageUri){
            result ->
            _uploadState.value = Success(result)
        }
    }


}