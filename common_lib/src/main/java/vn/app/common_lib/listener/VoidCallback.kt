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
class VoidCallback(
    private val action: () -> Unit
) {
    operator fun invoke(): Unit = action()

    override fun equals(other: Any?): Boolean = other is VoidCallback

    override fun hashCode(): Int = 0

    companion object {
        val EMPTY: VoidCallback = VoidCallback { }
    }
}
