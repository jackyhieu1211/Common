package vn.app.common_lib.extension

import android.content.Context
import java.io.File

object FileCommand {
    fun executeDelete(context: Context, paths: List<String>): Int {
        return try {
            val files = paths.map { File(it) }
            val fileCommands = files.joinToString(separator = " ") {
                if (it.isDirectory) "'${it.absolutePath}/'" else "'${it.absolutePath}'"
            }
            val command = "rm -rf $fileCommands"
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val result = process.waitFor()
            if (result == 0) {
                context.rescanPaths(paths)
            }
            result
        } catch (e: Exception) {
            -1
        }
    }
}