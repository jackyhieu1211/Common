package vn.app.common_lib.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import java.util.*

typealias BaseViewHolderFactory = (itemView: View) -> BaseViewHolder<*>

abstract class BaseAdapter<T : Item> : RecyclerView.Adapter<BaseViewHolder<*>>() {

    val items = mutableListOf<T>()

    fun clearItems() {
        items.size.run {
            items.clear()
            notifyItemRangeRemoved(0, this)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(list: List<T>) {
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T = items[position]

    fun getItemOrNull(position: Int): T? = items.getOrNull(position)

    abstract fun toViewType(viewType: Int): BaseViewType

    final override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        holder.bind(items[position], position)
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val holder = toViewType(viewType)
        val itemView = parent.inflate(holder.layoutRes)
        return holder.viewHolderFactory(itemView).apply {
            ViewTreeLifecycleOwner.get(parent)?.lifecycle?.addObserver(this)
        }
    }

    final override fun getItemViewType(position: Int): Int = items[position].viewType.type

    fun getItemViewTypeOrNull(position: Int): Int? = items.getOrNull(position)?.viewType?.type

    final override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }
}

fun ViewGroup.inflate(@LayoutRes l: Int): View {
    return LayoutInflater.from(context).inflate(l, this, false)
}

interface BaseViewType {
    val type: Int
    val layoutRes: Int
    val viewHolderFactory: BaseViewHolderFactory
}

interface Item {
    val id: String
    val viewType: BaseViewType

    companion object {
        fun generateId(): String = UUID.randomUUID().toString()
    }
}

