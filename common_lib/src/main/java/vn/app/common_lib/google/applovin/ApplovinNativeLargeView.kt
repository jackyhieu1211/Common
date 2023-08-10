package vn.app.common_lib.google.applovin

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import vn.app.common_lib.R
import vn.app.common_lib.rx.DestroyNativeAds
import vn.app.common_lib.rx.RxBus
import vn.app.common_lib.rx.receive
import vn.app.common_lib.extension.gone
import vn.app.common_lib.extension.isNetworkConnected
import vn.app.common_lib.extension.visible
import vn.app.common_lib.helper.AppPreference

class ApplovinNativeLargeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), KoinComponent {
    private val appPreference: AppPreference by inject()

    init {
        RxBus.receive(DestroyNativeAds::class.java) {
            destroyNativeAd()
        }
    }

//    private val binding: ApplovinNativeLargeBinding =
//        ApplovinNativeLargeBinding.inflate(LayoutInflater.from(context), this, false)

    private var nativeAdLoader: MaxNativeAdLoader? = null
    private var nativeAdView: MaxNativeAdView? = null
    private var nativeAd: MaxAd? = null

//    init {
//        loadAds(EnvConstant.AD_APPLOVIN_NATIVE_MODE)
//    }

    fun loadAds(adsId: String, loaded: ((Boolean) -> Unit)? = null) {
        this.visibility = VISIBLE
        if (!context.isNetworkConnected() || appPreference.isPremium) {
            this.gone()
            return
        }
        val binder =
            MaxNativeAdViewBinder.Builder(R.layout.applovin_native_large)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setAdvertiserTextViewId(R.id.advertiser_text_view)
                .setIconImageViewId(R.id.icon_image_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setOptionsContentViewGroupId(R.id.options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build()
        nativeAdView = MaxNativeAdView(binder, context)
        nativeAdLoader = MaxNativeAdLoader(adsId, context)

        nativeAdLoader?.setRevenueListener { }
        nativeAdLoader?.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
                // Cleanup any pre-existing native ad to prevent memory leaks.
                if (nativeAd != null) {
                    nativeAdLoader?.destroy(nativeAd)
                }
                loaded?.invoke(true)
                // Save ad for cleanup.
                nativeAd = ad
                // Add ad view to view.
                removeAllViews()
                addView(nativeAdView)
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                loaded?.invoke(false)
                gone()
            }

            override fun onNativeAdClicked(ad: MaxAd) {

            }

            override fun onNativeAdExpired(nativeAd: MaxAd?) {

            }
        })
        nativeAdLoader?.loadAd(nativeAdView)
    }

    fun destroyNativeAd() {
        // Must destroy native ad or else there will be memory leaks.
        if (nativeAd != null) {
            // Call destroy on the native ad from any native ad loader.
            nativeAdLoader?.destroy(nativeAd)
        }
        // Destroy the actual loader itself
        nativeAdLoader?.destroy()

    }

    fun showAds() {
        if (nativeAd != null) {
            visible()
        } else {
            gone()
        }
    }
}