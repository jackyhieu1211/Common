package vn.app.common_lib.extension

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.forEach
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration private constructor(builder: Builder) :
    RecyclerView.ItemDecoration() {

    private var gridLayoutManager: GridLayoutManager? = null
    private val includeEdge: Boolean
    private var horizontalSpacing: Int = 0
    private var verticalSpacing: Int = 0
    private val skipSpacingPosList = mutableListOf<Int>()

    init {
        includeEdge = builder.includeEdge
        val spacing = builder.spacing
        if (spacing != 0) {
            horizontalSpacing = spacing
            verticalSpacing = spacing
        } else {
            horizontalSpacing = builder.horizontalSpacing
            verticalSpacing = builder.verticalSpacing
        }
        skipSpacingPosList.run {
            clear()
            addAll(builder.skipSpacingPosList)
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (gridLayoutManager == null) {
            gridLayoutManager = parent.layoutManager as? GridLayoutManager ?: return
        }
        if (view.isLoadingItem()) return

        val spanCount = gridLayoutManager!!.spanCount
        val position = parent.getChildAdapterPosition(view)
        if (skipSpacingPosList.contains(position)) return

        if (position != -1) { // Fix when pull to refresh recyclerView
            val spanSize = gridLayoutManager!!.spanSizeLookup.getSpanSize(position)
            val column = gridLayoutManager!!.spanSizeLookup.getSpanIndex(position, spanCount)
            val totalChildCount = parent.adapter!!.itemCount
            val isLastRow = if (spanSize == 1) {
                position + spanCount - column > totalChildCount - 1
            } else {
                position - column / spanSize > totalChildCount - 1
            }
            val isFirstRow =
                gridLayoutManager!!.spanSizeLookup.getSpanGroupIndex(position, spanCount) == 0

            if (includeEdge) {
                outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount
                outRect.right = (column + spanSize) * horizontalSpacing / spanCount
                outRect.top = verticalSpacing
                outRect.bottom = if (isLastRow) verticalSpacing else 0
            } else {
                outRect.left = column * horizontalSpacing / spanCount
                outRect.right =
                    horizontalSpacing - (column + spanSize) * horizontalSpacing / spanCount
                outRect.top = if (isFirstRow) 0 else verticalSpacing
            }
        }
    }

    private fun View.isLoadingItem(): Boolean {
        if (this is ProgressBar) {
            return true
        } else if (this is ViewGroup) {
            this.forEach {
                if (it.isLoadingItem()) return true
            }
        }
        return false
    }

    class Builder {
        var includeEdge: Boolean = false
        var spacing = 0
        var verticalSpacing: Int = 0
        var horizontalSpacing: Int = 0
        val skipSpacingPosList = mutableListOf<Int>()

        fun includeEdge(includeEdge: Boolean): Builder {
            this.includeEdge = includeEdge
            return this
        }

        fun spacing(spacing: Int): Builder {
            this.spacing = spacing
            return this
        }

        fun verticalSpacing(verticalSpacing: Int): Builder {
            this.verticalSpacing = verticalSpacing
            return this
        }

        fun horizontalSpacing(horizontalSpacing: Int): Builder {
            this.horizontalSpacing = horizontalSpacing
            return this
        }

        fun skipSpacingForItems(vararg indexes: Int): Builder {
            skipSpacingPosList.run {
                clear()
                addAll(indexes.toList())
            }
            return this
        }

        fun build(): GridSpacingItemDecoration {
            return GridSpacingItemDecoration(this)
        }
    }

    companion object {

        fun newBuilder(): Builder {
            return Builder()
        }
    }
}