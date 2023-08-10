package vn.app.common_lib.listener

/**
 * A wrapper for an simple action callback to use in data state of an adapter.
 * This ensure equals/hashCode always same (true). So this will help diff-callback only compare
 * the display data.
 *
 * NOTES:
 * - Only use for wrapper action if it is used by the adapter which is used diff-callback.
 * - Again, the equals is always true for same type. And the hash code always is 0.
 */
class Fun1Callback<T>(
    private val action: (arg: T) -> Unit
) {
    operator fun invoke(arg: T): Unit = action(arg)

    override fun equals(other: Any?): Boolean = other is Fun1Callback<*>

    override fun hashCode(): Int = 0
}
