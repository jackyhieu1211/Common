package vn.app.common_lib.utils

import android.animation.PropertyValuesHolder

class AnimatorUtils private constructor() {
    companion object {
        const val ROTATION = "rotation"
        const val SCALE_X = "scaleX"
        const val SCALE_Y = "scaleY"
        const val TRANSLATION_X = "translationX"
        const val TRANSLATION_Y = "translationY"
    }

    private fun rotation(vararg values: Float): PropertyValuesHolder {
        return PropertyValuesHolder.ofFloat(ROTATION, *values)
    }

    private fun translationX(vararg values: Float): PropertyValuesHolder {
        return PropertyValuesHolder.ofFloat(TRANSLATION_X, *values)
    }

    private fun translationY(vararg values: Float): PropertyValuesHolder {
        return PropertyValuesHolder.ofFloat(TRANSLATION_Y, *values)
    }

    private fun scaleX(vararg values: Float): PropertyValuesHolder {
        return PropertyValuesHolder.ofFloat(SCALE_X, *values)
    }

    private fun scaleY(vararg values: Float): PropertyValuesHolder {
        return PropertyValuesHolder.ofFloat(SCALE_Y, *values)
    }
}
