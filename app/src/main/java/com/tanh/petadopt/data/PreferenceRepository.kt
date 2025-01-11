package com.tanh.petadopt.data

import com.google.apphosting.datastore.testing.DatastoreTestTrace.FirestoreV1Action.Listen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.tanh.petadopt.domain.dto.PetDto
import com.tanh.petadopt.domain.model.Result
import com.tanh.petadopt.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("LABEL_NAME_CLASH")
class PreferenceRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    private val collection = firestore.collection(Util.PREFERENCES_COLLECTION)

    fun getPreferenceByUser(userId: String): Flow<com.tanh.petadopt.domain.model.Result<List<PetDto>, Exception>> {
        return callbackFlow {
            var snapshotStateListener: ListenerRegistration? = null
            var petListener: ListenerRegistration? = null
            try {
                snapshotStateListener = collection.whereEqualTo("userId", userId)
                    .addSnapshotListener { favoriteSnapshot, errorSnapshot ->
                        if(errorSnapshot != null) {
                            trySend(Result.Error(errorSnapshot)).isSuccess
                            return@addSnapshotListener
                        }

                        if(favoriteSnapshot != null) {
                            val favoritePetIds = favoriteSnapshot.documents.mapNotNull { it["petId"].toString() }
                            petListener = firestore.collection(Util.ANIMALS_COLLECTION)
                                .addSnapshotListener { petSnapshot, petError ->
                                    if(petError != null) {
                                        trySend(Result.Error(petError)).isSuccess
                                        return@addSnapshotListener
                                    }

                                    if(petSnapshot != null) {
                                        val pets = petSnapshot.documents.map {doc ->
                                            PetDto(
                                                animalId = doc.getString("animalId") ?: "",
                                                ownerId = doc.getString("ownerId") ?: "",
                                                name = doc.getString("name") ?: "",
                                                age = doc.getDouble("age") ?: 0.0,
                                                weight = doc.getDouble("weight") ?: 0.0,
                                                breed = doc.getString("breed") ?: "",
                                                category = doc.getString("category") ?: "",
                                                gender = doc.getBoolean("gender") ?: false,
                                                photoUrl = doc.getString("photoUrl") ?: "",
                                                isFavorite = favoritePetIds.contains(doc.id)
                                            )
                                        }
                                        trySend(Result.Success(pets)).isSuccess
                                    }
                                }
                        }
                    }
            } catch (e: Exception) {
                trySend(Result.Error(e))
            }
            awaitClose {
                snapshotStateListener?.remove()
                petListener?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getPetPreferencesByCategory(userId: String, category: String): Flow<com.tanh.petadopt.domain.model.Result<List<PetDto>, Exception>> {
        return callbackFlow {
            var snapshotStateListener: ListenerRegistration? = null
            var petListener: ListenerRegistration? = null
            try {
                snapshotStateListener = collection.whereEqualTo("userId", userId)
                    .addSnapshotListener { favoriteSnapshot, errorSnapshot ->
                        if(errorSnapshot != null) {
                            trySend(Result.Error(errorSnapshot)).isSuccess
                            return@addSnapshotListener
                        }

                        if(favoriteSnapshot != null) {
                            val favoritePetIds = favoriteSnapshot.documents.mapNotNull { it["petId"].toString() }

                            petListener = firestore.collection(Util.ANIMALS_COLLECTION)
                                .whereEqualTo("category", category)
                                .addSnapshotListener { petSnapshot, petError ->
                                    if(petError != null) {
                                        trySend(Result.Error(petError)).isSuccess
                                        return@addSnapshotListener
                                    }

                                    if(petSnapshot != null) {
                                        val pets = petSnapshot.documents.map {doc ->
                                            PetDto(
                                                animalId = doc.getString("animalId") ?: "",
                                                ownerId = doc.getString("ownerId") ?: "",
                                                name = doc.getString("name") ?: "",
                                                age = doc.getDouble("age") ?: 0.0,
                                                weight = doc.getDouble("weight") ?: 0.0,
                                                breed = doc.getString("breed") ?: "",
                                                category = doc.getString("category") ?: "",
                                                gender = doc.getBoolean("gender") ?: false,
                                                photoUrl = doc.getString("photoUrl") ?: "",
                                                isFavorite = favoritePetIds.contains(doc.id)
                                            )
                                        }
                                        trySend(Result.Success(pets)).isSuccess
                                    }
                                }
                        }
                    }
            } catch (e: Exception) {
                trySend(Result.Error(e))
            }
            awaitClose {
                snapshotStateListener?.remove()
                petListener?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addToFavorite(userId: String, petId: String) {
        withContext(Dispatchers.IO) {
            val newFavorite = hashMapOf(
                "userId" to userId,
                "petId" to petId
            )
            collection.add(newFavorite)
        }
    }

    suspend fun removeFromFavorite(userId: String, petId: String) {
        withContext(Dispatchers.IO) {
            collection.whereEqualTo("userId", userId)
                .whereEqualTo("petId", petId)
                .get()
                .addOnSuccessListener { snapshot ->
                    for( doc in snapshot.documents) {
                        doc.reference.delete()
                    }
                }
        }
    }

}