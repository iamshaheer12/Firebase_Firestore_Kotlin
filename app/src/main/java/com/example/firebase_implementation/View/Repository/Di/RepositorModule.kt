package com.example.firebase_implementation.View.Repository.Di

import com.example.firebase_implementation.View.Repository.authRepository
import com.example.firebase_implementation.View.Repository.authRepositoryImpl
import com.example.firebase_implementation.View.Repository.noteRepository
import com.example.firebase_implementation.View.Repository.noteRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RepositorModule {


    @Provides
    @Singleton
    fun ProvideNoteRepository(
        database:FirebaseFirestore
    ):noteRepository{

        return noteRepositoryImpl(database)
    }
    @Provides
    @Singleton
    fun ProvideAuthRepository(
        database:FirebaseFirestore,
        auth: FirebaseAuth
    ):authRepository{

        return authRepositoryImpl(database,auth)
    }
}