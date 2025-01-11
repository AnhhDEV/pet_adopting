package com.tanh.petadopt.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import com.microsoft.azure.storage.blob.CloudBlobClient
import com.microsoft.azure.storage.blob.CloudBlockBlob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AzureBlobStorage @Inject constructor(
    private val blobClient: CloudBlobClient,
    private val context: Context
) {

     suspend fun uploadFileToAzureBlob(uri: Uri, containerTag: String): String {
        try {
            val container = blobClient.getContainerReference(containerTag)
            val filename = "${System.currentTimeMillis()}.png"
            val blob = container.getBlockBlobReference(filename)
            val contentType = "image/png"
            blob.properties.contentType = contentType
            uploadFile(uri = uri, blob = blob)
            return blob.uri.toString()
        } catch (e: Exception) {
            Log.d("azure1", e.message.toString())
            return ""
        }
    }

    @SuppressLint("Recycle")
    private suspend fun uploadFile(uri: Uri, blob: CloudBlockBlob) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if(inputStream != null) {
                    blob.upload(inputStream, context.contentResolver.openAssetFileDescriptor(uri, "r")!!.length)
                } else {
                    Log.d("azure1", "!")
                }
                Log.d("azure1", "Successfully")
                inputStream?.close()
            } catch (e: Exception) {
                Log.d("azure1", e.message.toString())
            }
        }
    }

    private fun identifyMimeType(uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri) ?: "No type"
        return when(mimeType) {
            "image/jpeg" -> "image/png"
            "video/mp4" -> "video/mp4"
            else -> "application/octet-stream"
        }
    }

}