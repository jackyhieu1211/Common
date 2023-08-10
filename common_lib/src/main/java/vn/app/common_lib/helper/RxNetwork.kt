package vn.app.common_lib.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import io.reactivex.rxjava3.core.Single
import java.io.InterruptedIOException
import java.net.ProtocolException
import java.net.SocketException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import java.nio.channels.ClosedChannelException
import javax.net.ssl.SSLException

object RxNetwork {

    fun checkNetwork(context: Context): Single<Boolean> {
        return Single.fromCallable { context.isNetworkConnected() }
            .flatMap { connected ->
                if (connected) {
                    Single.just(true)
                } else {
                    Single.error(NoNetworkException())
                }
            }
    }
}

fun Context?.isNetworkConnected(): Boolean {
    this ?: return false
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    cm ?: return false

    val network = cm.activeNetwork
    if (network != null) {
        val networkCapabilities = cm.getNetworkCapabilities(network)
        return networkCapabilities != null
    }

    return false
}

fun Context.isMobileConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    cm ?: return false

    val network = cm.activeNetwork
    if (network != null) {
        val networkCapabilities = cm.getNetworkCapabilities(network)
        return networkCapabilities != null
                && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    return false
}

fun Throwable.isNetworkError(): Boolean {
    return this is NoNetworkException || this is SocketException || this is ClosedChannelException
            || this is InterruptedIOException || this is ProtocolException || this is SSLException
            || this is UnknownHostException || this is UnknownServiceException
}

open class AppException(
    override val message: String = "",
    override val cause: Throwable = Throwable(""),
    val status: Int = -99
) : Throwable(message, cause)

class NoNetworkException(
    message: String = "",
    cause: Throwable = Throwable("")
) : AppException(message, cause)