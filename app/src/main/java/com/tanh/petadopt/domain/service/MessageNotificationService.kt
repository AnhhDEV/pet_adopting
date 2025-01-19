package com.tanh.petadopt.domain.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.google.firebase.Timestamp
import com.tanh.petadopt.R
import com.tanh.petadopt.data.ChatRepository
import com.tanh.petadopt.data.GoogleAuthUiClient
import com.tanh.petadopt.domain.model.onError
import com.tanh.petadopt.domain.model.onSuccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class MessageNotificationService : Service() {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var auth: GoogleAuthUiClient

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> {
                start()
                var count = 0
                val userId = auth.getSignedInUser()?.userId ?: ""
                coroutineScope.launch {
                    try {
                        chatRepository.getChatsService(userId = userId).collect { result ->
                            result.run {
                                onSuccess { chats ->
                                    chats.forEach { chat ->
                                        coroutineScope.launch {
                                            val sender = async {
                                                chatRepository.getUser(chat.fromId ?: "")?.username ?: "Anonymous"
                                            }.await()
                                            Log.d("MNS", "fromId: ${chat.fromId}, userId: ${userId}")
                                            if (chat.fromId != userId && calculateTime(chat.lastTime ?: Timestamp.now())) {
                                                Log.d("MNS", "running${++count}")
                                                sendNotification(
                                                    content = chat.lastMessage ?: "No content",
                                                    userName = sender
                                                )
                                            }
                                        }
                                    }
                                }
                                onError {
                                    Log.d("MNS", it.message.toString())
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("MNS", e.message.toString())
                    }
                }
            }

            Actions.STOP.toString() -> stopSelf()
        }

        return START_STICKY
    }

    private fun sendNotification(content: String, userName: String) {
        val id = System.currentTimeMillis().toInt()
        val notification = Notification.Builder(this, "start_message")
            .setSmallIcon(R.drawable.inbox)
            .setContentTitle("You get a new message from $userName")
            .setContentText(content)
            .build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }

    private fun start() {
        val notification = Notification.Builder(this, "start_message")
            .setSmallIcon(R.drawable.dog)
            .setContentTitle("Pet adopting is running")
            .build()
        startForeground(1, notification)
    }

    private fun calculateTime(lastTime: Timestamp): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val currentTimeDate = LocalDateTime.now()
            val lastTimeDate =
                lastTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val timeDifference = Duration.between(lastTimeDate, currentTimeDate).toSeconds()
            Log.d("MNS", "time: $timeDifference")
            return timeDifference in (0..10)
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    enum class Actions {
        START, STOP
    }

}