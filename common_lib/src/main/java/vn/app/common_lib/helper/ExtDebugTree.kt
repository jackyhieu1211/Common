package vn.app.common_lib.helper

import android.text.TextUtils
import android.util.Log
import timber.log.Timber

class ExtDebugTree : Timber.DebugTree() {

    companion object {
        private const val MAX_LOG_LENGTH = 4000
        private const val CALLER_INFO_FORMAT = " at %s(%s:%s)"
    }

    private var showLink = true
    private var callerInfo = ""

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (showLink) {
            callerInfo = getCallerInfo(Throwable().stackTrace)
        }
        if (message.length < MAX_LOG_LENGTH) {
            printSingleLine(priority, tag, message + callerInfo)
        } else {
            printMultipleLines(priority, tag, message)
        }
    }

    private fun printMultipleLines(priority: Int, tag: String?, message: String) {
        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = newline.coerceAtMost(i + MAX_LOG_LENGTH)
                val part = message.substring(i, end)
                printSingleLine(priority, tag, part)
                i = end
            } while (i < newline)
            i++
        }
        if (showLink && !TextUtils.isEmpty(callerInfo)) {
            printSingleLine(priority, tag, callerInfo)
        }
    }

    private fun printSingleLine(priority: Int, tag: String?, message: String) {
        if (priority != Log.ASSERT) {
            Log.println(priority, tag, message)
        }
    }


    private fun getCallerInfo(stacks: Array<StackTraceElement>?): String {
        return if (stacks == null || stacks.size < 5) {
            ""
        } else formatForLogCat(stacks[5])
    }

    private fun formatForLogCat(stack: StackTraceElement): String {
        val className = stack.className
        val packageName = className.substring(0, className.lastIndexOf("."))
        return String.format(CALLER_INFO_FORMAT, packageName, stack.fileName, stack.lineNumber)
    }
}