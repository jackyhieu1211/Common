package vn.app.common_lib.extension

import android.widget.SeekBar

fun SeekBar.onSetProgressChanged(onChanged: (Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, value: Int, isUser: Boolean) {
            if (isUser) onChanged.invoke(value)
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    })
}

fun SeekBar.onSetOpacityChanged(onChanged: (opacity: Float, opacityView: String) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, value: Int, isUser: Boolean) {
            if (isUser && max > 0) {
                val opacityView = "${(100 * value.toFloat() / max.toFloat()).toInt()}%"
                onChanged.invoke(
                    value.toFloat() / max.toFloat(),
                    opacityView
                )
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    })
}