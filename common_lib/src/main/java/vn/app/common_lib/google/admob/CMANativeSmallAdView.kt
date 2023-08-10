package vn.app.common_lib.google.admob

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import vn.app.common_lib.extension.gone
import vn.app.common_lib.extension.isNetworkConnected
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import vn.app.common_lib.databinding.CmaNativeSmallAdViewBinding
import vn.app.common_lib.helper.AppPreference


class CMANativeSmallAdView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), KoinComponent {

    private val appPreference: AppPreference by inject()
    private var nativeAd: NativeAd? = null

    private val binding: CmaNativeSmallAdViewBinding =
        CmaNativeSmallAdViewBinding.inflate(LayoutInflater.from(context), this)


    fun loadCMAAd(idAd: String, loaded: (() -> Unit)? = null) {
        Log.e("TAG", "loadCMAAd: --> $idAd")
        this.visibility = VISIBLE
        if (!context.isNetworkConnected() || appPreference.isPremium) {
            this.gone()
            return
        }

        val adLoader = AdLoader.Builder(context, idAd)
            .forNativeAd {
                binViewNativeAd(it)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    this@CMANativeSmallAdView.gone()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    loaded?.invoke()
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun binViewNativeAd(nativeAd: NativeAd) {
        this.nativeAd = nativeAd

        with(binding) {
            nativeAdView.visibility = VISIBLE
            nativeAdView.callToActionView = callToActionView
            nativeAdView.headlineView = primaryView
            primaryView.text = nativeAd.headline
            callToActionView.text = nativeAd.callToAction
            tertiaryView.text = nativeAd.body
            nativeAdView.bodyView = tertiaryView

            if (nativeAd.icon != null) {
                iconView.visibility = VISIBLE
                iconView.setImageDrawable(nativeAd.icon?.drawable)
            } else {
                iconView.visibility = GONE
            }

            //  Set the secondary view to be the star rating if available.
            //  Set the secondary view to be the star rating if available.
            val starRating = nativeAd.starRating
            if (starRating != null && starRating > 0) {
                secondaryView.visibility = GONE
                ratingBar.visibility = VISIBLE
                ratingBar.rating = starRating.toFloat()
                nativeAdView.starRatingView = ratingBar
            } else {
                secondaryView.text = getSecondaryText(nativeAd)
                secondaryView.visibility = VISIBLE
                ratingBar.visibility = GONE
            }

            nativeAdView.setNativeAd(nativeAd);
        }
    }


    private fun getSecondaryText(nativeAd: NativeAd): String {
        return if (adHasOnlyStore(nativeAd)) {
            binding.nativeAdView.storeView = binding.secondaryView
            nativeAd.store.orEmpty()
        } else if (!TextUtils.isEmpty(nativeAd.advertiser)) {
            binding.nativeAdView.advertiserView = binding.secondaryView
            nativeAd.advertiser.orEmpty()
        } else {
            ""
        }
    }

    private fun adHasOnlyStore(nativeAd: NativeAd): Boolean {
        return !TextUtils.isEmpty(nativeAd.store) && TextUtils.isEmpty(nativeAd.advertiser)
    }

    fun destroyNativeAd() {
        nativeAd?.destroy()
        Log.e("TAG", "destroyNativeAd")
    }

}