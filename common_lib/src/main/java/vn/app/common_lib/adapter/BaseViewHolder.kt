package vn.app.common_lib.adapter

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(
    itemView: View
) : RecyclerView.ViewHolder(itemView), DefaultLifecycleObserver {

    var current: T? = null
        private set

    @Suppress("UNCHECKED_CAST")
    fun bind(obj: Any?) {
        val data = obj as T
        current = data
        onBind(data)
    }

    @Suppress("UNCHECKED_CAST")
    fun bind(obj: Any?, position: Int) {
        val data = obj as T
        current = data
        onBind(data, position)
    }

    open fun onBind(data: T) = Unit

    open fun onBind(data: T, position: Int) = Unit

    /**
     * Triggered when this view holder is being recycled.
     * This place is recommend to cancel/unsubscribe to the running task.
     */
    open fun onRecycled() = Unit

}
