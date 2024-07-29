package com.example.firebase_implementation.View.di

import UriTypeAdapter
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.firebase_implementation.View.Utils.SharedPref
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object appModule {


    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context:Context):SharedPreferences{
      // the reason of using mode private i want to store the latest values on the place of old values
       return context.getSharedPreferences(SharedPref.localPref,Context.MODE_PRIVATE)
    }
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return  GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
            .create()
    }
}