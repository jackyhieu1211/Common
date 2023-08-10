package vn.app.common_lib.extension

import android.app.Activity
import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.*
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import vn.app.common_lib.utils.ToastUtil

@Suppress("DEPRECATION")
fun Activity.deviceSize(): Pair<Int, Int> {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    val metrics = windowManager.currentWindowMetrics
    // Legacy size that Display#getSize reports
    val bounds = metrics.bounds
    // Gets all excluding insets
    val windowInsets = metrics.windowInsets
    val insets = windowInsets.getInsetsIgnoringVisibility(
        WindowInsets.Type.navigationBars()
            or WindowInsets.Type.displayCutout()
    )
    val insetsWidth = insets.right + insets.left
    val insetsHeight = insets.top + insets.bottom

    return Pair(bounds.width() - insetsWidth, bounds.height() - insetsHeight)
}

fun Activity.deviceWidth(): Int = deviceSize().first
fun Activity.deviceHeight(): Int = deviceSize().second

// IMPORTANT - check this if it correct on release version
fun <T> Context.isBaseActivityOfTask(activity: Class<T>): Boolean {
    val am = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    return am?.appTasks?.firstOrNull()?.taskInfo?.baseActivity?.className == activity.name
}

fun FragmentActivity.startActivityForResult(
    onResultOK: ((Intent?) -> Unit)? = null,
    onCancel: (() -> Unit)? = null
): ActivityResultLauncher<Intent> =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> onResultOK?.invoke(result.data)
            Activity.RESULT_CANCELED -> onCancel?.invoke()
        }
    }

fun Fragment.startActivityForResult(
    onResultOK: ((Intent?) -> Unit)? = null,
    onCancel: (() -> Unit)? = null
): ActivityResultLauncher<Intent> =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> onResultOK?.invoke(result.data)
            Activity.RESULT_CANCELED -> onCancel?.invoke()
        }
    }


/**
 * Convenience extension method to register a [ContentObserver] given a lambda.
 */
fun ContentResolver.registerObserver(
    observer: (selfChange: Boolean) -> Unit
): ContentObserver {
    val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }

    registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver)
    registerContentObserver(MediaStore.Files.getContentUri("external"), true, contentObserver)
    return contentObserver
}

fun Context.openAppOnCHPlay() {
    val it: Intent = try {
        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))

    } catch (error: ActivityNotFoundException) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
    }

    startActivity(it)
}

fun Activity.activateLockScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestDismissKeyguard(this@activateLockScreen, null)
        }
    }
}

fun Activity.deactivateLockScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(false)
        setTurnScreenOn(false)
    } else {
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
}


fun Fragment.activateLockScreen() {
    val activity = activity ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        activity.setShowWhenLocked(true)
        activity.setTurnScreenOn(true)
    } else {
        activity.window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }

    with(activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestDismissKeyguard(activity, null)
        }
    }
}

fun Fragment.deactivateLockScreen() {
    val activity = activity ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        activity.setShowWhenLocked(false)
        activity.setTurnScreenOn(false)
    } else {
        activity.window.clearFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
}

fun Activity.bringToBackground() {
    val intent = Intent().apply {
        action = Intent.ACTION_MAIN
        addCategory(Intent.CATEGORY_HOME)
//			addCategory(Intent.CATEGORY_DEFAULT)
//			flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}

fun Fragment.bringToBackground() {
    val intent = Intent().apply {
        action = Intent.ACTION_MAIN
        addCategory(Intent.CATEGORY_HOME)
//			addCategory(Intent.CATEGORY_DEFAULT)
//			flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}


fun Activity.copyToClipboardManager(content: String) {
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    clipboard?.setPrimaryClip(ClipData.newPlainText("", content))
    ToastUtil.showToast(this, "Copied text to clipboard")
}