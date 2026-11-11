package com.byted.myfitafy.di

import com.byted.myfitafy.repository.ClothingRepository
import com.byted.myfitafy.repository.ClothingRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideClothingRepository(firestore: FirebaseFirestore): ClothingRepository {
        return ClothingRepositoryImpl(firestore)
    }
}