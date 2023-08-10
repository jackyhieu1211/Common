package vn.app.common_lib.extension

import android.os.Environment
import java.lang.IllegalArgumentException

fun getInfoStorage(filePath: String = Environment.getExternalStorageDirectory().absolutePath): String {
    return try {
        val totalMemory = DeviceMemory.getTotalMemoryInBytes(filePath = filePath)
        val freeMemory = DeviceMemory.getAvailableMemoryInBytes(filePath = filePath)
        val usedMemory = totalMemory - freeMemory
        String.format(
            "%s/%s",
            DeviceMemory.displaySize(usedMemory),
            DeviceMemory.displaySize(totalMemory)
        )
    } catch (e: IllegalArgumentException) {
        String.format("%s/%s", "0GB", "0GB")
    }
}

fun getPercentStore(filePath: String = Environment.getExternalStorageDirectory().absolutePath): Int {
    return try {
        DeviceMemory.getPercentUsed(filePath = filePath)
    } catch (e: IllegalArgumentException) {
        0
    }
}