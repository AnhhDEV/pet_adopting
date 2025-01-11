package com.tanh.petadopt.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.tanh.petadopt.domain.model.Chat
import com.tanh.petadopt.domain.model.Message
import com.tanh.petadopt.domain.model.Result
import com.tanh.petadopt.domain.model.UserData
import com.tanh.petadopt.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("LABEL_NAME_CLASH")
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val userCollection = firestore.collection(Util.USERS_COLLECTION)
    private val chatCollection = firestore.collection(Util.CHATS_COLLECTION)

    //xóa tin nhắn đã đọc
    suspend fun changeStatusMessage(chatId: String, userId: String) {
        withContext(Dispatchers.IO) {
            try {

                val pair = mapOf(
                    "isRead" to true
                )

                val messages = chatCollection
                    .document(chatId)
                    .collection(Util.MESSAGE_COLLECTION)
                    .whereEqualTo("isRead", false)
                    .whereNotEqualTo("uid", userId)
                    .get()
                    .await()

                for(document in messages.documents) {
                    chatCollection
                        .document(chatId)
                        .collection(Util.MESSAGE_COLLECTION)
                        .document(document.id)
                        .update(pair)
                        .await()
                }

            } catch (e: Exception) {
                Log.d("isread", e.localizedMessage)
            }
        }
    }

    //lấy tin nhắn chưa đọc
    fun getUnreadMessage(userId: String): Flow<Result<Int, Exception>> {
        return callbackFlow<Result<Int, Exception>> {
            Log.d("unread", "repo: $userId")
            val chatListeners = mutableListOf<ListenerRegistration>()
            var chatStateSnapshot: ListenerRegistration? = null
            try {
                chatStateSnapshot = chatCollection
                    .whereArrayContains("id", userId)
                    .addSnapshotListener { chatSnapshot, error ->
                        if(error != null) {
                            trySend(Result.Error(error)).isSuccess
                            return@addSnapshotListener
                        }
                        chatListeners.forEach { it.remove() }
                        chatListeners.clear()
                        var unreadMessage = 0
                        Log.d("unread", "run: ${chatSnapshot?.size()}")
                        chatSnapshot?.toObjects(Chat::class.java)?.mapNotNull { it }?.forEach { chat ->
                            val chatId = chat.chatId ?: return@forEach
                            val messageListener = chatCollection
                                .document(chatId)
                                .collection(Util.MESSAGE_COLLECTION)
                                .whereEqualTo("isRead", false)
                                .whereNotEqualTo("uid", userId)
                                .addSnapshotListener { messageSnapshot, error ->
                                    if(error != null) {
                                        trySend(Result.Error(error)).isSuccess
                                        Log.d("unread", "error: ${error.message}")
                                        return@addSnapshotListener
                                    }
                                    unreadMessage += messageSnapshot?.size() ?: 0
                                    Log.d("unread", "success: $unreadMessage")
                                    trySend(Result.Success(unreadMessage)).isSuccess
                                }
                            chatListeners.add(messageListener)
                        }
                    }
            } catch (e: Exception) {
                trySend(Result.Error(e)).isSuccess
            }
            awaitClose {
                chatListeners.forEach { it.remove() }
                chatStateSnapshot?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }
    //if conversation exist return chatId else null
    suspend fun getChatId(fromId: String, toId: String): String? {
        return try {
            Log.d("detail", "repo: $fromId, $toId")
            val querySnapshot = chatCollection
                .whereArrayContains("id", fromId)
                .get()
                .await()
            Log.d("detail", "result: ${querySnapshot.toObjects(Chat::class.java).joinToString { it.toString() }}")

            return querySnapshot.toObjects(Chat::class.java)
                .firstOrNull { chat ->
                    chat.id.contains(toId)
                }
                ?.chatId
        } catch (e: Exception) {
            null
        }
    }

    //lịch sử tin nhắn
    fun getMessages(chatId: String): Flow<Result<List<Message>, Exception>> {
        return callbackFlow<Result<List<Message>, Exception>> {
            var snapshotStateListener: ListenerRegistration? = null
            try {
                snapshotStateListener = firestore
                    .collection(Util.CHATS_COLLECTION)
                    .document(chatId)
                    .collection(Util.MESSAGE_COLLECTION)
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(100)
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            trySend(Result.Error(exception ?: Exception("Not found"))).isSuccess
                            return@addSnapshotListener
                        }
                        val response = if (snapshot != null) {
                            val messages = snapshot.toObjects(Message::class.java).mapNotNull { it }
                            Result.Success(messages)
                        } else {
                            Result.Error(Exception("empty list"))
                        }
                        trySend(response).isSuccess
                    }

            } catch (e: Exception) {
                trySend(Result.Error(e))
            }
            awaitClose {
                snapshotStateListener?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    //danh sách chat của user
    fun getChats(userId: String): Flow<Result<List<Chat>, Exception>> {
        return callbackFlow {
            var snapshotStateListener: ListenerRegistration? = null
            try {
                snapshotStateListener = chatCollection
                    .whereArrayContains("id", userId)
                    .orderBy("lastTime", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            trySend(
                                Result.Error(
                                    exception ?: Exception("Not found chat")
                                )
                            ).isSuccess
                            return@addSnapshotListener
                        }
                        val response = if (snapshot != null) {
                            val chats = snapshot.toObjects(Chat::class.java).mapNotNull { chat ->
                                Log.d("repo", "repo: " + userId)
                                val ids = chat.id
                                if (ids.size == 2) {
                                    val fromId = ids.firstOrNull { it == userId }
                                    val toId = ids.firstOrNull { it != userId }
                                    if (fromId != null && toId != null) {
                                        chat.copy(
                                            fromId = fromId,
                                            toId = toId
                                        )
                                    } else {
                                        null
                                    }
                                } else {
                                    null
                                }
                            }
                            Result.Success(chats)
                        } else {
                            Result.Success(emptyList())
                        }
                        trySend(response).isSuccess
                    }
            } catch (e: Exception) {
                trySend(Result.Error(e))
            }
            awaitClose {
                snapshotStateListener?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    //tạo message
    suspend fun createMessage(chatId: String, message: Message) {
        try {
            withContext(Dispatchers.IO) {
                val newMessage = hashMapOf(
                    "content" to message.content,
                    "uid" to message.uid,
                    "time" to message.time,
                    "isRead" to message.isRead
                )
                firestore
                    .collection(Util.CHATS_COLLECTION)
                    .document(chatId)
                    .collection(Util.MESSAGE_COLLECTION)
                    .add(newMessage)
                    .await()

                val updates = mapOf(
                    "lastMessage" to message.content,
                    "lastTime" to message.time
                )
                chatCollection.document(chatId).update(updates).await()
            }
        } catch (e: Exception) {
            Log.d("ChatRepository", "createMessage: ${e.message}")
        }
    }

    //tạo chat mới khi lần đầu nhắn tin
    suspend fun createChat(fromId: String, toId: String): String {
        return try {
            withContext(Dispatchers.IO) {
                val fromUser = getUser(id = fromId)
                val toUser = getUser(id = toId)

                if (fromUser != null && toUser != null) {
                    val newChatId = chatCollection.document().id
                    val chat = hashMapOf(
                        "chatId" to newChatId,
                        "lastMessage" to "",
                        "lastTime" to null,
                        "id" to listOf(fromId, toId),
                    )
                    chatCollection.document(newChatId).set(chat).await()

                    return@withContext newChatId
                } else {
                    Log.d("ChatRepository", "createChat: User not found")
                    return@withContext ""
                }
            }
        } catch (e: Exception) {
            Log.d("ChatRepository", "createChat: ${e.message}")
            return ""
        }
    }

    //lấy user theo id
    suspend fun getUser(id: String): UserData? {
        return try {
            var user: UserData? = null
            val userSnapShot = userCollection.whereEqualTo("userId", id).get().await()
            if (userSnapShot != null) {
                user = userSnapShot.documents.first().toObject(UserData::class.java) ?: UserData()
            }
            user
        } catch (e: Exception) {
            return null
        }
    }

}