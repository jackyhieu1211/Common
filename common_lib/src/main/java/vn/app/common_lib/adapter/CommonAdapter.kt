package vn.app.common_lib.adapter

import androidx.annotation.LayoutRes

class CommonAdapter : BaseAdapter<CommonViewType>() {
    override fun toViewType(viewType: Int): BaseViewType {
        return ViewType.values()[viewType]
    }
}

enum class ViewType(
    @LayoutRes override val layoutRes: Int,
    override val viewHolderFactory: BaseViewHolderFactory,
) : BaseViewType {

}

sealed class CommonViewType(
    override val viewType: ViewType
) : Item {

}