package com.tanh.petadopt.di

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.blob.CloudBlobClient
import com.tanh.petadopt.data.AzureBlobStorage
import com.tanh.petadopt.data.ChatRepository
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.data.PetRepository
import com.tanh.petadopt.data.PreferenceRepository
import com.tanh.petadopt.data.UserRepository
import com.tanh.petadopt.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBlobClient(): CloudBlobClient {
        return try {
            val storageAccount = CloudStorageAccount.parse(Util.CONNECTION_STRING)
            storageAccount.createCloudBlobClient()
        } catch (e: Exception) {
            Log.d("DI", "Error creating CloudBlobClient: ${e.message}")
            throw IllegalStateException("Unable to create CloudBlobClient", e)
        }
    }

    @Provides
    @Singleton
    fun provideAzureBlobStorage(@ApplicationContext context: Context) = AzureBlobStorage(provideBlobClient(), context)

    @Provides
    @Singleton
    fun provideGoogleAuthClient(@ApplicationContext context: Context) = GoogleAuthUiClient(context)

    @Provides
    @Singleton
    fun provideFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideFireauth() = Firebase.auth

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore) = UserRepository(firestore)

    @Provides
    @Singleton
    fun providePetRepository(firestore: FirebaseFirestore) = PetRepository(firestore)

    @Provides
    @Singleton
    fun providePreferenceRepository(firestore: FirebaseFirestore) = PreferenceRepository(firestore)

    @Provides
    @Singleton
    fun provideChatRepository(firestore: FirebaseFirestore) = ChatRepository(firestore)

}