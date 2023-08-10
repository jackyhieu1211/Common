package vn.app.common_lib.extension

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import vn.app.common_lib.R

fun SwipeRefreshLayout.setupDefault(onRefresh: () -> Unit) {
    setColorSchemeResources(R.color.colorPrimary)
    setOnRefreshListener { onRefresh() }
    resources.getDimensionPixelSize(R.dimen.swipe_refresh_offset).run {
        setProgressViewOffset(false, -this, this)
    }
}
