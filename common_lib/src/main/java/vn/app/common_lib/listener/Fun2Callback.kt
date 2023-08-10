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
class Fun2Callback<T0, T1>(
    private val action: (arg0: T0, arg1: T1) -> Unit
) {
    operator fun invoke(arg0: T0, arg1: T1) = action(arg0, arg1)

    override fun equals(other: Any?): Boolean = other is Fun2Callback<*, *>

    override fun hashCode(): Int = 0
}
