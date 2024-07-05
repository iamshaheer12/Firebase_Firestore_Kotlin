package com.example.firebase_implementation.View.Repository.Di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {
    @Provides
    @Singleton
    fun providerFireStoreInstance():FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }
}