package com.example.walled.core.data.source

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.example.walled.core.domain.model.Image
import com.example.walled.core.domain.model.Result
import com.example.walled.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MediaManager(
    private val context : Context
) {

    private val logger = Logger("DownloadManager")


    suspend fun getImages(limit : Int = 10): Result<List<Image>>{
        val imageList  = mutableListOf<Image>()
        return withContext(Dispatchers.IO){
            try {
                val resolver = context.contentResolver
                val collection =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL
                        )
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                val projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE
                )
                val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

                val query = resolver.query(
                    collection,
                    projection,
                    null,
                    null,
                    sortOrder
                )
                query?.use { cursor ->
                    // Cache column indices.
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                    while (cursor.moveToNext()) {
                        // Get values of columns for a given video.
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val size = cursor.getInt(sizeColumn)

                        val contentUri: Uri = ContentUris.withAppendedId(
                            collection,
                            id
                        )

                        imageList += Image(contentUri)

                    }

                }
                Result.Success(imageList)

            }catch (e : Exception){
                e.printStackTrace()
                Result.Error(e)
            }

        }

    }
    suspend fun downloadRemoteImage(
        url : String,
        onProgress : suspend (Int,Float) ->Unit,
        block : suspend (String) -> HttpResponse
    ): Uri?{
        Log.d("DownloadManager", "downloadRemoteImage: invoked")
         withContext(Dispatchers.IO){
            try{
                val response = block(url)
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

    suspend fun downloadFileToCache(
        url : String,
        onProgress : (Int) -> Unit,
        block : suspend (String) -> HttpResponse
    ) : Result<Uri>{
        return withContext(Dispatchers.IO){
            try {
                val cacheDir = context.cacheDir
                val tempFile = createTempFile(context,"wallpapers")

                val response = block(url)
                val totalBytes = response.headers["Content-Length"]?.toLong() ?: -1L

                // 2. Stream the download directly to the cache file
                tempFile.outputStream().use { outputStream ->
                    val channel = response.bodyAsChannel()
                    val buffer = ByteArray(8 * 1024)
                    var bytesCopied = 0L
                    while (!channel.isClosedForRead) {
                        val read = channel.readAvailable(buffer)
                        if (read == -1) break
                        outputStream.write(buffer, 0, read)
                        bytesCopied += read
                        if (totalBytes > 0) {
                            val progress = (bytesCopied * 100 / totalBytes).toInt()
                            onProgress(progress)
                        }
                    }
                }
                val authority = "${context.packageName}.fileprovider"
                val contentUri = FileProvider.getUriForFile(context, authority, tempFile)

                return@withContext Result.Success(contentUri)
            }catch (e : Exception){
                e.printStackTrace()
                logger.error(e.message.toString())
                return@withContext Result.Error(e)
            }

        }
    }

    fun createTempFile(
        context: Context,
        directory : String = "images"
    ): File {
        val imageDirPath = File(context.cacheDir, directory).apply {
            if (!exists()) {
                mkdir()
            }
        }
        val tempImageFile = File.createTempFile("temp_", ".jpg", imageDirPath)
        return tempImageFile
    }
}