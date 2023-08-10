package vn.app.common_lib.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import timber.log.Timber
import vn.app.common_lib.R
import vn.app.common_lib.utils.ToastUtil
import java.io.File
import java.io.IOException
import kotlin.math.roundToInt


fun Context.colorList(id: Int): ColorStateList {
    return ColorStateList.valueOf(ContextCompat.getColor(this, id))
}

fun Context.openAppSystemSettings(packageName: String) {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    })
}

fun isGTAndroid11(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}

//Android11
fun isAndroidR(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}

fun isGTAndroid10(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

fun isAndroid13(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

fun Context?.isPostNotificationAllow(): Boolean {
    if (this == null) return true
    return if (isAndroid13()) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun Context.openAppNotificationSettings() {
    val intent = Intent().apply {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            else -> {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                addCategory(Intent.CATEGORY_DEFAULT)
                data = Uri.parse("package:$packageName")
            }
        }
    }
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Timber.e(e)
    }

}

private fun Context.getUriFromUrl(packageName: String, url: String): Uri {
    return FileProvider.getUriForFile(
        this,
        "$packageName.provider",
        File(url)
    )
}

fun Context.loadJSONFromAsset(fileName: String): String {
    return try {
        assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ex: IOException) {
        ex.printStackTrace()
        return ""
    }
}

fun Context.isAcceptStoragePermission(): Boolean {
    if (isGTAndroid11()) {
        return Environment.isExternalStorageManager()
    }

    val listPermissionAndroidBelow10 = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    return hasPermissions(listPermissionAndroidBelow10)
}

fun Context.hasPermissions(permissions: Array<String>): Boolean {
    var isAccept = true
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isAccept = false
            break
        }
    }
    return isAccept
}

fun Context.rescanPath(path: String, callback: (() -> Unit)? = null) {
    rescanPaths(arrayListOf(path), callback)
}

// avoid calling this multiple times in row, it can delete whole folder contents
fun Context.rescanPaths(paths: List<String>, callback: (() -> Unit)? = null) {
    if (paths.isEmpty()) {
        callback?.invoke()
        return
    }

    var cnt = paths.size
    MediaScannerConnection.scanFile(applicationContext, paths.toTypedArray(), null) { _, _ ->
        if (--cnt == 0) {
            callback?.invoke()
        }
    }
}

fun Context.rateOnCHPlay(packageName: String) {
    val intent = try {
        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
    } catch (_: ActivityNotFoundException) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
    }
    startActivity(intent)
}

@SuppressLint("MissingPermission")
fun Context?.isNetworkConnected(): Boolean {
    if (this == null) return false
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    cm ?: return false
    val network = cm.activeNetwork
    if (network != null) {
        val networkCapabilities = cm.getNetworkCapabilities(network)
        return networkCapabilities != null
    }
    return false
}

fun Fragment.shareMultipleFile(packageName: String, filePath: String) {
    val context = context ?: return
    val files = ArrayList<Uri>()
    files.add(context.getUriFromUrl(packageName, filePath))
    shareWithUri(files)
}

fun Fragment.shareMultipleFile(packageName: String, filePaths: List<String>) {
    val context = context ?: return
    val files = ArrayList<Uri>()
    filePaths.forEach {
        files.add(context.getUriFromUrl(packageName, it))
    }
    shareWithUri(files)
}

fun Activity.shareMultipleFile(packageName: String, filePaths: List<String>) {
    val files = ArrayList<Uri>()
    filePaths.forEach {
        files.add(getUriFromUrl(packageName, it))
    }
    shareWithUri(files)
}

fun Activity.shareMultipleFile(packageName: String, filePath: String) {
    val files = ArrayList<Uri>()
    files.add(getUriFromUrl(packageName, filePath))
    shareWithUri(files)
}

private fun Fragment.shareWithUri(listUri: ArrayList<Uri>) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        type = "*/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, listUri)
    }
    try {
        startActivity(Intent.createChooser(intent, "Share"))
    } catch (e: ActivityNotFoundException) {
        ToastUtil.showToast(context, e.message.orEmpty())
    }
}

private fun Activity.shareWithUri(listUri: ArrayList<Uri>) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        type = "*/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, listUri)
    }
    try {
        startActivity(Intent.createChooser(intent, "Share"))
    } catch (e: ActivityNotFoundException) {
        ToastUtil.showToast(this, e.message.orEmpty())
    }
}

fun Context.launchUrl(url: String) {
    launchChromeTab(url)
}

private fun CustomTabsIntent.launchSafeUrl(
    context: Context, url: String, onNoBrowser: (() -> Unit)? = null
) {
    val webUrl =
        if (url.startsWith("http://").not() && url.startsWith("https://").not())
            "https://$url" else url
    val uri = Uri.parse(webUrl)

    intent.data = uri
    if (intent.resolveActivity(context.packageManager) != null) {
        launchUrl(context, uri)
    } else {
        Timber.e("The browser is not exist!")
        context.openLink(webUrl)
        onNoBrowser?.invoke()
//        ToastUtil.showToast(context, R.string.toast_no_browser)
    }
}

fun Context.openLink(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Timber.e(e)
    }
}

fun Context.launchChromeTab(url: String?, onNoBrowser: (() -> Unit)? = null) {
    url ?: return
    val intentBuilder = CustomTabsIntent.Builder()
    val params = CustomTabColorSchemeParams.Builder()
        .setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBgApp))
        .setToolbarColor(ContextCompat.getColor(this, R.color.colorBgApp))
        .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorBgApp))
        .build()
    intentBuilder.setDefaultColorSchemeParams(params)
    val customTabsIntent = intentBuilder.build()
    customTabsIntent.launchSafeUrl(this, url, onNoBrowser)
}

fun String.isMimeTypeArchive(itemPath: String): Boolean {
    val MIME_TYPE_RAR = "application/rar"
    val MIME_TYPE_RAR_COMPRESSED = "application/x-rar-compressed"
    val MIME_TYPE_ZIP = "application/zip"
    val MIME_TYPE_7Z = "application/x-7z-compressed"
    val MIME_TYPE_TAR = "application/x-tar"
    val MIME_TYPE_BZIP = "application/x-bzip"
    val MIME_TYPE_BZIP_2 = "application/x-bzip2"
    val MIME_TYPE_GZIP = "application/gzip"
    val MIME_TYPE_VND_RAR = "application/vnd.rar"
    val MIME_TYPE_WIM = "wim"
    val MIME_TYPE_JAR = "application/java-archive"
    return itemPath.endsWith(MIME_TYPE_WIM) ||
            itemPath.endsWith("apk") ||
            itemPath.endsWith("tar") ||
            itemPath.endsWith("rar") ||
            itemPath.endsWith("zip") ||
            itemPath.endsWith("7z") ||
            itemPath.endsWith("jar") ||
            itemPath.endsWith("bz2") ||
            itemPath.endsWith("bzip2") ||
            itemPath.endsWith("tbz2") ||
            itemPath.endsWith("tbz") ||
            itemPath.endsWith("gz") ||
            itemPath.endsWith("gzip") ||
            itemPath.endsWith("tgz") ||
            itemPath.endsWith("swm") ||
            itemPath.endsWith("xz") ||
            itemPath.endsWith("txz") ||
            itemPath.endsWith("zipx") ||
            this == MIME_TYPE_ZIP ||
            this == MIME_TYPE_RAR ||
            this == MIME_TYPE_RAR_COMPRESSED ||
            this == MIME_TYPE_7Z ||
            this == MIME_TYPE_TAR ||
            this == MIME_TYPE_BZIP ||
            this == MIME_TYPE_BZIP_2 ||
            this == MIME_TYPE_GZIP ||
            this == MIME_TYPE_VND_RAR ||
            this == MIME_TYPE_JAR
}

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }

fun Context.applicationIsInstall(packageName: String): Boolean {
    return try {
        val packageInfo = packageManager.getPackageInfoCompat(packageName)
        packageInfo.applicationInfo.enabled
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun Context.openOnCHPlay(packageName: String) {
    val intent = try {
        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
    } catch (_: ActivityNotFoundException) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
    }
    startActivity(intent)
}

fun Context.openCHPlayForceUpdate(packageName: String) {
    openOnCHPlay(packageName)
}

fun Context.shareApp(packageName: String, appName: String, messageShare: String) {
    val appStoreLink = "https://play.google.com/store/apps/details?id=$packageName"
    val share = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, appName)
        putExtra(Intent.EXTRA_TEXT, messageShare)
    }
    try {
        startActivity(Intent.createChooser(share, appName))
    } catch (e: ActivityNotFoundException) {
        ToastUtil.showToast(this, e.message.orEmpty())
    }
}

fun Context.dpToPx(dp: Float): Int = (dp * resources.displayMetrics.density).roundToInt()