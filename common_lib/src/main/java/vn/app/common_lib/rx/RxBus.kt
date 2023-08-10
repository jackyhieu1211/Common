package vn.app.common_lib.rx

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit


object RxBus {
	private val publisher = PublishSubject.create<Any>()
	fun send(event: Any) {
		publisher.onNext(event)
	}

	fun <T : Any> toObservable(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}

fun <T : RxEvent> RxBus.receive(eventType: Class<T>, action: (event: T) -> Unit): Disposable {
	return toObservable(eventType)
		.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread())
		.subscribe(
			{
				action(it)
			},
			{
				Timber.e(it)
			}
		)
}

fun <T : RxEvent> RxBus.receiveDelay(
	eventType: Class<T>,
	action: (event: T) -> Unit
): Disposable {
	return toObservable(eventType)
		.throttleFirst(1000L, TimeUnit.MILLISECONDS)
		.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread())
		.subscribe(
			{
				action(it)
			},
			{
				Timber.e(it)
			}
		)
}