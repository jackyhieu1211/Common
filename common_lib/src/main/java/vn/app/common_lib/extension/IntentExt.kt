package vn.app.common_lib.extension

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.database.getStringOrNull
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

fun Intent?.getPathFromUri(context: Context): String {
    try {

        val storageRoot = "/storage/emulated/0"
        val intent = this ?: return ""
        val dataUri = intent.data ?: return ""
        Timber.e("AAAAA dataString = $dataUri")
        val path = intent.data?.path.orEmpty()
        if (path.isBlank()) return ""

        if (path.contains("/document/primary:")) {
            val documentPath = path.replace("/document/primary:", "/storage/emulated/0/")
            val replacePath = documentPath.replaceBefore(storageRoot, "")
            if (File(replacePath).checkFileOK()) return replacePath
        }

        if (path.contains("/document/raw:")) {
            val documentPath = path.replace("/document/raw:", "/storage/emulated/0/")
            val replacePath = documentPath.replaceBefore(storageRoot, "")
            if (File(replacePath).checkFileOK()) return replacePath
        }

        if (dataUri.authority == "com.android.providers.downloads.documents") {
            val id = DocumentsContract.getDocumentId(dataUri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                java.lang.Long.valueOf(id)
            )
            val pathFromCursor = context.getPathFromUriData(contentUri)
            if (pathFromCursor.isNotBlank() && File(pathFromCursor).checkFileOK()) {
                return File(pathFromCursor).absolutePath
            }
        }

        // có 1 số app lấy ra path luôn thì dùng cái này
        val filePath = File(path)
        if (filePath.checkFileOK()) return filePath.absolutePath

        if (path.contains(storageRoot)) {
            // có 1 số app lấy ra path luôn nhưng có thêm mấy cái lằng nhằng ở trước thì dùng cái này
            val replacePath = path.replaceBefore(storageRoot, "")
            if (File(replacePath).checkFileOK()) return replacePath
        }

        // có 1 số app lấy ra path thiếu cái root_path thì thử thêm rootPath vào
        val listPath = mutableListOf<String>()
        intent.data?.pathSegments.orEmpty().forEachIndexed { index, text ->
            if (index != 0) {
                listPath.add(text)
            }
        }
        if (listPath.isNotEmpty()) {
            Timber.e("AAAAA listPath = $listPath")
            listPath.add(0, storageRoot)
            val finalPath2 = listPath.joinToString(separator = "/") { it }
            if (File(finalPath2).checkFileOK()) return finalPath2
        }

        if (dataUri.scheme == "content") {
            val pathFromCursor = context.getPathFromUriData(dataUri)
            if (pathFromCursor.isNotBlank() && File(pathFromCursor).checkFileOK()) {
                return File(pathFromCursor).absolutePath
            }
        }
        return ""
    } catch (e: Exception) {
        Timber.e(e)
        return ""
    }
}

fun Context.getPathFromUriData(uri: Uri): String {
    var pathFromCursor = ""
    val returnCursor: Cursor? =
        contentResolver.query(uri, null, null, null, null)
    val columnIndex =
        returnCursor?.getColumnIndex(MediaStore.Files.FileColumns.DATA) ?: -1
    returnCursor?.moveToFirst()
    if (columnIndex >= 0) {
        pathFromCursor = returnCursor?.getStringOrNull(columnIndex).orEmpty()
    }
    returnCursor?.close()
    return pathFromCursor
}

fun download(
    context: Context,
    uri: Uri,
    temPath: String,
    progress: (Long, Int) -> Unit
): String {
    try {
        val temporaryFile = File(temPath)
        if (temporaryFile.exists().not()) {
            temporaryFile.mkdirs()
        }
        val name = queryName(context, uri).orEmpty()
        Timber.e("AAAAA query name = $name")
        if (name.isBlank()) return ""
        val destinationFilename = File(temporaryFile, name)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destinationFilename).use { output ->
                val buffer = ByteArray(4096)
                var bytesRead = input.read(buffer)
                val length = input.available()
                Timber.e("AAAAA length = $length")
                var bytesCopied = 0L
                while (bytesRead >= 0) {
                    output.write(buffer, 0, bytesRead)
                    bytesCopied += bytesRead
                    bytesRead = input.read(buffer)
                    progress.invoke(bytesCopied, length)
                }
                output.flush()
                return destinationFilename.absolutePath
            }
        }
        return ""
    } catch (ex: Exception) {
        Timber.e("AAAAAA download error 3= ${ex.message.orEmpty()}")
        ex.printStackTrace()
        return ""
    }
}

fun queryName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex =
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                result = cursor.getString(nameIndex)
            }
        }
    }
    if (result == null) {
        result = uri.lastPathSegment
    }
    if (result?.endsWith(".") == true) {
        // từ thằng WhatsApp lấy về. Ko lấy đc đuổi Pdf. nên phải add Pdf vào
        result = "${result}pdf"
    }
    return result
}

fun File.checkFileOK(): Boolean {
    return isFile && exists()
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}