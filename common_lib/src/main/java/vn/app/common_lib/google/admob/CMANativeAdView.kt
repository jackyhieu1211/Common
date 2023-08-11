package vn.app.common_lib.google.admob
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import vn.app.common_lib.databinding.CmaNativeAdViewBinding
import vn.app.common_lib.helper.AppPreference
import vn.app.common_lib.helper.isNetworkConnected


class CMANativeAdView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), KoinComponent {

    private val appPreference: AppPreference by inject()

    private val binding: CmaNativeAdViewBinding =
        CmaNativeAdViewBinding.inflate(LayoutInflater.from(context), this)
    private var nativeAd: NativeAd? = null

    fun loadCMAAd(idAd: String, loaded: ((success: Boolean) -> Unit)? = null) {
        this.visibility = VISIBLE
        if (!context.isNetworkConnected() || appPreference.isPremium) {
            this.isGone = true
            // showDefault(idImage)
            return
        }

        val adLoader = AdLoader.Builder(context, idAd)
            .forNativeAd {
                binViewNativeAd(it)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.e("AAAAA onAdFailedToLoad = ${adError.message.orEmpty()}")
                    this@CMANativeAdView.isGone = true
                    loaded?.invoke(false)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    loaded?.invoke(true)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun binViewNativeAd(nativeAd: NativeAd) {
        with(binding) {
            nativeAdView.visibility = VISIBLE
            nativeAdView.callToActionView = callToActionView
            nativeAdView.headlineView = primaryView
            nativeAdView.mediaView = mediaView
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

            binding.adNotificationView.isVisible = true
            binding.callToActionView.isVisible = true

            /*if (viewPlaceholder.isShimmerStarted) {
                viewPlaceholder.visibility = View.GONE
                viewPlaceholder.stopShimmer()
            }*/

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

            nativeAdView.setNativeAd(nativeAd)
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
    }
}