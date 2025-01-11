package com.tanh.petadopt.data

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.tanh.petadopt.domain.model.SignInResult
import com.tanh.petadopt.domain.model.UserData
import com.tanh.petadopt.util.Util
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

@Suppress("DEPRECATION")
class GoogleAuthUiClient(
    private val context: Context
) {
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private val auth = Firebase.auth

    //Lần đầu tiên đăng nhập. Giao tiếp qua intent. Lúc này gửi intent sender.
    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    //Nhận intent. Dữ liệu người dùng dưới dạng credential. Nhận intent qua OnActivityResult.
    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString(),
            gmail = email
        )
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true) //Ứng dụng hỗ trợ google token ID
                    .setFilterByAuthorizedAccounts(false)  //Hiển thị những tài khoản được uỷ quyền.
                    .setServerClientId(Util.DEFAULT_WEB_CLIENT_ID) //Server web client ID
                    .build()
            )
            .setAutoSelectEnabled(true) //Tự động chọn tài khoản nếu chỉ có 1 tài khoản trên app
            .build()
    }
}