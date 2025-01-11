package com.tanh.petadopt.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.tanh.petadopt.domain.dto.PetDto
import com.tanh.petadopt.domain.model.Pet
import com.tanh.petadopt.domain.model.Result
import com.tanh.petadopt.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PetRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection(Util.ANIMALS_COLLECTION)

    fun getPetsByUser(userId: String): Flow<Result<List<Pet>, Exception>> {
        return callbackFlow<Result<List<Pet>, Exception>> {
            var snapshotStateListener: ListenerRegistration? = null
            try {
                snapshotStateListener = collection
                    .whereEqualTo("ownerId", userId)
                    .addSnapshotListener {value, error ->
                        val response = if(value != null) {
                            val pets = value.toObjects(Pet::class.java).mapNotNull { it }
                            Result.Success(pets)
                        } else {
                            Result.Error(error = error ?: Exception("Unknown error"))
                        }
                        trySend(response)
                    }
            } catch (e: Exception) {
                trySend(Result.Error(e))
            }
            awaitClose {
                snapshotStateListener?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getAllPets(): Flow<Result<List<Pet>, Exception>> {
        return callbackFlow {
            var snapshotStateListener: ListenerRegistration? = null
            try {
                snapshotStateListener = collection
                    .orderBy("category")
                    .addSnapshotListener { value, error ->
                        val response = if (value != null) {
                            val pets = value.toObjects(Pet::class.java).mapNotNull { it }
                            Result.Success(pets)
                        } else {
                            Result.Error(error = error ?: Exception("Unknown error"))
                        }
                        trySend(response)
                    }
            } catch (e: Exception) {
                trySend(Result.Error(e))
            }
            awaitClose {
                snapshotStateListener?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getAllPetsByCategory(category: String): Flow<Result<List<Pet>, Exception>> {
        return callbackFlow<Result<List<Pet>, Exception>> {
            var snapshotStateListener: ListenerRegistration? = null
            try {
                snapshotStateListener = collection
                    .orderBy("age")
                    .whereEqualTo("category", category)
                    .addSnapshotListener { value, error ->
                        val response = if (value != null) {
                            val pets = value.toObjects(Pet::class.java).mapNotNull { it }
                            Result.Success(pets)
                        } else {
                            Result.Error(error = error ?: Exception("Unknown error"))
                        }
                        trySend(response)
                    }
            } catch (e: Exception) {
                trySend(Result.Error(e))
            }
            awaitClose {
                snapshotStateListener?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPetById(petId: String, userId: String): Result<PetDto, Exception> {
        return try {
            withContext(Dispatchers.IO) {
                val petSnapshot = firestore.collection(Util.ANIMALS_COLLECTION)
                    .document(petId)
                    .get()
                    .await()

                val pet = petSnapshot.toObject(Pet::class.java)
                    ?: return@withContext Result.Error(Exception("Pet not found"))

                var petDto = PetDto(
                    animalId = pet.animalId,
                    ownerId = pet.ownerId,
                    name = pet.name,
                    age = pet.age,
                    weight = pet.weight,
                    breed = pet.breed,
                    category = pet.category,
                    gender = pet.gender,
                    photoUrl = pet.photoUrl,
                    address = pet.address,
                    about = pet.about,
                    isFavorite = false
                )

                val preferencesSnapshot = firestore.collection(Util.PREFERENCES_COLLECTION)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("petId", petId)
                    .get()
                    .await()

                if (preferencesSnapshot.documents.isNotEmpty()) {
                    petDto = petDto.copy(isFavorite = true)
                }

                Result.Success(petDto)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun insertPet(
        ownerId: String,
        name: String,
        age: Double,
        weight: Double,
        breed: String,
        category: String,
        gender: Boolean,
        photoUrl: String,
        address: String,
        about: String
    ): Result<Boolean, Exception> {
        return try {
            withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    val documentId = collection.document().id
                    val newPet = Pet(
                        animalId = documentId,
                        ownerId = ownerId,
                        name = name,
                        age = age,
                        weight = weight,
                        breed = breed,
                        category = category,
                        gender = gender,
                        photoUrl = photoUrl,
                        address = address,
                        about = about
                    )
                    collection.document(documentId)
                        .set(newPet)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                continuation.resume(Result.Success(true))
                            } else {
                                continuation.resume(
                                    Result.Error(
                                        it.exception ?: Exception("Unknown error")
                                    )
                                )
                            }
                        }
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}