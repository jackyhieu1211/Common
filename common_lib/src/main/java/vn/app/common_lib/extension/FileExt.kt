package vn.app.common_lib.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File


// File : current file pdf
fun File.fileName(tempFolder: File): File {
    var num = 0
    val extensionFile = "pdf"
    var save = "${nameWithoutExtension}_signed.$extensionFile"
    var file = File(tempFolder, save)
    while (file.exists()) {
        save = "${nameWithoutExtension}_signed_${num++}.${extensionFile}"
        file = File(tempFolder, save)
    }
    return file
}

fun Context.listFileFromAsset(dirFrom: String): List<String> {
    val result = mutableListOf<String>()
    val fileList = resources.assets.list(dirFrom)
    if (fileList != null) {
        for (i in fileList.indices) {
            result.add(fileList[i])
        }
    }
    return result
}

fun Context.getBitmapFromAsset(position: Int, name: String): Bitmap {
    val folder = if (position == 0) "icon" else "img_stamp"
    return assets
        .open("$folder/$name")
        .use(BitmapFactory::decodeStream)
}