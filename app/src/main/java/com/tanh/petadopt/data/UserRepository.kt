package com.tanh.petadopt.data

import com.google.firebase.firestore.FirebaseFirestore
import com.tanh.petadopt.domain.model.Result
import com.tanh.petadopt.domain.model.UserData
import com.tanh.petadopt.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection(Util.USERS_COLLECTION)

    suspend fun getUser(userId: String): Result<UserData, Exception> {
        return try {
            withContext(Dispatchers.IO) {
                suspendCoroutine {continuation ->
                    collection.whereEqualTo("userId", userId).get()
                        .addOnSuccessListener { snapshot ->
                            if(!snapshot.isEmpty) {
                                val userData = snapshot.documents.first().toObject(UserData::class.java)
                                if(userData != null) {
                                    continuation.resume(Result.Success(userData))
                                } else {
                                    continuation.resume(Result.Error(Exception("User data is null")))
                                }
                            } else {
                                continuation.resume(Result.Error(Exception("User not found")))
                            }
                        }
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun insertUser(
        name: String?,
        id: String,
        photoUrl: String?
    ): Result<Boolean, Exception> {
        return try {
            withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    val newDocumentId = collection.document().id
                    val userData = UserData(id, name, photoUrl)
                    collection.document(newDocumentId).set(userData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                continuation.resume(
                                    com.tanh.petadopt.domain.model.Result.Success(
                                        true
                                    )
                                )
                            } else {
                                continuation.resume(
                                    com.tanh.petadopt.domain.model.Result.Error(
                                        it.exception ?: Exception("Unknown error")
                                    )
                                )
                            }
                        }
                }
            }
        } catch (e: Exception) {
            com.tanh.petadopt.domain.model.Result.Error(e)
        }
    }

}