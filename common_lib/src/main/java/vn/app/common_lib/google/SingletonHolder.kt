package vn.app.common_lib.google

open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T =
        instance ?: synchronized(this) {
            instance ?: constructor(arg).also { instance = it }
        }
}

open class MySingletonHolder<out T, in A, in B>(private val constructor: (A, B) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A, arg2: B): T =
        instance ?: synchronized(this) {
            instance ?: constructor(arg, arg2).also { instance = it }
        }
}