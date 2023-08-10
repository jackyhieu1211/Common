package vn.app.common_lib.extension


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

fun Array<out Observable<Boolean>>.validateAll(onResult: (Boolean) -> Unit): Disposable {
    return Observable.combineLatestArray(this) { conditions ->
        conditions.all { it == true }
    }
        .subscribe(
            {
                onResult(it)
            },
            {
                Timber.e(it)
            }
        )
}

fun CompositeDisposable.delay(timeMillis: Long, endListener: () -> Unit) {
    return Single.timer(timeMillis, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                endListener()
            },
            {
                Timber.e(it)
            }
        )
        .autodispose(this)
}

fun Disposable.autodispose(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}