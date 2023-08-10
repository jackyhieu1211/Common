package vn.app.common_lib.extension

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import vn.app.common_lib.R

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .into(this)
}

fun ImageView.loadImage(resId: Int?) {
    Glide.with(this.context)
        .load(resId)
        .into(this)
}

fun ImageView.loadImageBitmap(bitmap: Bitmap?) {
    Glide.with(this.context)
        .load(bitmap)
        .apply(RequestOptions().fitCenter())
        .into(this)
}

fun ImageView.loadImage(any: Any?) {
    Glide.with(this.context)
        .load(any)
        .apply(RequestOptions().fitCenter())
        .into(this)
}

fun ImageView.loadRoundImage(url: String, radius: Int) {
    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions().centerCrop())
        .transform(RoundedCorners(radius))
        .into(this)
}

fun ImageView.loadRoundImage(resId: Int, radius: Int) {
    Glide.with(this.context)
        .load(resId)
        .apply(RequestOptions().centerCrop().dontAnimate())
        .transform(RoundedCorners(radius))
        .into(this)
}

fun ImageView.setEnable(isEnable: Boolean) {
    this.isEnabled = isEnable
}


fun ImageView.loadAPK(drawable: Drawable?) {
    val radius = context.resources.getDimensionPixelSize(R.dimen.space_8dp)
    Glide.with(this)
        .load(drawable)
        .transform(RoundedCorners(radius))
        .centerCrop()
        .error(R.drawable.icon_apk_default)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .into(this)
}

fun ImageView.loadImageStorage(any: Any?) {
    Glide.with(this.context)
        .load(any)
        .apply(RequestOptions().centerCrop())
        .placeholder(R.drawable.ic_file_default)
        .error(R.drawable.ic_file_default)
        .into(this)
}

fun ImageView.loadImageAsset(url: String) {
    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions().centerInside())
        .into(this)
}


//fun ImageView.loadImageBitmap(bitmap: Bitmap?, rotateRotationAngle: Float) {
//    Glide.with(this.context)
//        .load(bitmap)
//        .apply(RequestOptions().fitCenter())
//        .transform(RotateTransformation(rotateRotationAngle))
//        .into(this)
//}