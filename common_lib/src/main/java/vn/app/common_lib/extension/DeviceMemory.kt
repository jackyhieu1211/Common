package vn.app.common_lib.extension

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import java.text.DecimalFormat

object DeviceMemory {
	private val decim: DecimalFormat by lazy { DecimalFormat("#.##") }
	private val statFs: StatFs by lazy { StatFs(Environment.getExternalStorageDirectory().absolutePath) }

	fun getStorageTotal(change: Long): String {
		//StatFs statFs = new StatFs("/data");
		val total = statFs.blockCountLong.toDouble() * statFs.blockSizeLong.toDouble() / change
		return decim.format(total) + "GB"
	}

	fun getFreeSpace(change: Long): String {
		//StatFs statFs = new StatFs("/data");
		val free = statFs.availableBlocksLong.toDouble() * statFs.blockSizeLong.toDouble() / change
		return decim.format(free) + "GB"
	}

	fun getUsedSpace(change: Long): String {
		//StatFs statFs = new StatFs("/data");
		val total = statFs.blockCountLong.toDouble() * statFs.blockSizeLong.toDouble() / change
		val free = statFs.availableBlocksLong.toDouble() * statFs.blockSizeLong.toDouble() / change
		return decim.format(total - free) + "GB"
	}

	fun getPercentStore(change: Long): Int {
		//StatFs statFs = new StatFs("/data");
		val total = statFs.blockCountLong.toDouble() * statFs.blockSizeLong.toDouble() / change
		val free = statFs.availableBlocksLong.toDouble() * statFs.blockSizeLong.toDouble() / change
		val busy = total - free
		return (busy / total * 100).toInt()
	}

	fun percentUse(activity: Activity): Int {
		val activityManager =
			activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val memoryInfo = ActivityManager.MemoryInfo()
		activityManager.getMemoryInfo(memoryInfo)
		val percentAvail = memoryInfo.availMem / memoryInfo.totalMem.toDouble()

		return ((1 - percentAvail) * 100).toInt()
	}

	fun getAvailableMemoryInBytes(filePath: String): Long {
		val stat = StatFs(filePath)
		return stat.blockSizeLong * stat.availableBlocksLong
	}

	fun getTotalMemoryInBytes(filePath: String): Long {
		val stat = StatFs(filePath)
		return stat.blockSizeLong * stat.blockCountLong
	}

	fun getPercentUsed(filePath: String): Int {
		val totalMemory: Long = getTotalMemoryInBytes(filePath)
		val freeMemory: Long = getAvailableMemoryInBytes(filePath)
		val usedMemory = (totalMemory - freeMemory).toFloat()
		return ((usedMemory / totalMemory) * 100).toInt()
	}

	fun displaySize(bytes: Long): String {
//		val memoryDetails: String = displaySize(freeMemory)
//			.toString() + " free out of " + displaySize(totalMemory)
		return if (bytes > 1073741824) String.format(
			"%.02f",
			bytes.toFloat() / 1073741824
		) + " GB" else if (bytes > 1048576) String.format(
			"%.02f",
			bytes.toFloat() / 1048576
		) + " MB" else if (bytes > 1024) String.format(
			"%.02f",
			bytes.toFloat() / 1024
		) + " KB" else "$bytes B"
	}
}