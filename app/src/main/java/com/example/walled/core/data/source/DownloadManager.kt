package com.example.walled.core.data.source

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.walled.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadManager(
    private val context : Context,
    private val client : HttpClient
) {

    private val logger = Logger("DownloadManager")
    suspend fun downloadRemoteImage(
        url : String,
        onProgress : suspend (Int,Float) ->Unit
    ): Uri?{
        Log.d("DownloadManager", "downloadRemoteImage: invoked")
         withContext(Dispatchers.IO){
            try{
                val response = client.get(url)
                val totalBytes = response.headers["Content-Length"]?.toLong() ?: -1L
                logger.info("Total bytes of the payload : ${totalBytes/1024/1024}")

                val resolver = context.contentResolver
                val collection =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL
                        )
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Walled")
                }
                val itemUri = resolver.insert(collection, contentValues)
                itemUri?.let { uri ->
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        val channel = response.bodyAsChannel() // get byte stream
                        val buffer = ByteArray(8 * 1024)
                        var bytesCopied = 0L
                        while (!channel.isClosedForRead) {
                            val read = channel.readAvailable(buffer)
                            if (read == -1) break
                            outputStream.write(buffer, 0, read)
                            bytesCopied += read
                            if (totalBytes > 0) {
                                val progress = (bytesCopied * 100 / totalBytes).toInt()
                                onProgress(progress,(totalBytes.toDouble()/1024/1024).toFloat())
                            }
                        }
                    }
                }
                return@withContext itemUri

            }catch (e : Exception){
                e.printStackTrace()
                throw Exception(e)
            }
        }

        return null

    }
}