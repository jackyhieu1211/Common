package vn.app.common_lib.google.admob

import android.content.Context
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.isGone
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import vn.app.common_lib.google.MySingletonHolder
import vn.app.common_lib.helper.AppPreference
import vn.app.common_lib.helper.isNetworkConnected

class CMABannerUtil(private val context: Context, private val appPreference: AppPreference) {

    companion object :
        MySingletonHolder<CMABannerUtil, Context, AppPreference>(::CMABannerUtil)

    private fun LinearLayout.getAdSize(): AdSize {
        val density = context.resources.displayMetrics.density
        val adWidthPixel = if (this.width > 0) {
            this.width
        } else context.resources.displayMetrics.widthPixels

        val adWidth = (adWidthPixel / density).toInt()
        if (adWidth > 0) {
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
        }

        return AdSize.LARGE_BANNER
    }

    fun loadBanner(adUnit: String, viewAds: LinearLayout) {
        if (appPreference.isPremium) {
            viewAds.isGone = true
            return
        }

        val adView = AdView(context)
        adView.adUnitId = adUnit
        if (context.isNetworkConnected()) {
            adView.setAdSize(viewAds.getAdSize())
            val adViewParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            adViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL or RelativeLayout.CENTER_VERTICAL)
            // adViewParams.setMargins(0, 4, 0, 0);
            adViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            viewAds.addView(adView, adViewParams)

            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } else {
            viewAds.isGone = true
        }
    }

}