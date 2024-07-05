package com.example.firebase_implementation.View.Utils
// To manage UI states we use sealed class
// provide controlled inheritance of your class hierarchies
//direct subclasses of a sealed class are known at compile time
sealed class UiStates<out T> {
    object Loading:UiStates<Nothing>()
    data class Success<out T>(
        val data:T
    ):UiStates<T>()
    data class Failure(
        val error:String
    ):UiStates<Nothing>()
}