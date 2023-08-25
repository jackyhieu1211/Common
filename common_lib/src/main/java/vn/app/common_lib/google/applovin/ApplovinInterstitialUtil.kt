package vn.app.common_lib.google.applovin

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import vn.app.common_lib.extension.isNetworkConnected
import vn.app.common_lib.google.MySingletonHolder
import vn.app.common_lib.helper.AppPreference

class ApplovinInterstitialUtil(
    private val activity: Activity,
    private val appPreference: AppPreference
) : MaxAdListener, MaxAdRevenueListener {

    companion object :
        MySingletonHolder<ApplovinInterstitialUtil, Activity, AppPreference>(::ApplovinInterstitialUtil)

    /**
     * countLoadAds: Mỗi lần load failed cho load lại tối đa 3 lần
     */
    private var countLoadAds: Int = 0


    private var interstitialAd: MaxInterstitialAd? = null
    private var onShowAdCompleteListener: OnShowAdCompleteListener? = null


    fun loadAds(adId: String) {
        if (activity.isNetworkConnected().not()) {
            return
        }
        if (appPreference.isPremium) {
            return
        }
        if (interstitialAd == null) {
            interstitialAd = MaxInterstitialAd(adId, activity)
            interstitialAd?.setListener(this)
            interstitialAd?.setRevenueListener(this)
            // Load the first ad.
            countLoadAds++
            interstitialAd?.loadAd()
        } else {
            if (interstitialAd?.isReady == false) {
                interstitialAd?.loadAd()
            }
        }
    }

    fun showAds(adId: String, onShowAdCompleteListener: OnShowAdCompleteListener) {
        this.onShowAdCompleteListener = onShowAdCompleteListener
        if (activity.isNetworkConnected().not()) {
            onShowAdCompleteListener.onShowAdComplete()
            return
        }
        if (appPreference.isPremium) {
            onShowAdCompleteListener.onShowAdComplete()
            return
        }
        if (interstitialAd?.isReady == true) {
            if (appPreference.isTimeAdValid()) {
                interstitialAd?.showAd()
            }
        } else {
            loadAds(adId = adId)
            onShowAdCompleteListener.onShowAdComplete()
        }
    }

    fun forceShowAds(adId: String, onShowAdCompleteListener: OnShowAdCompleteListener) {
        this.onShowAdCompleteListener = onShowAdCompleteListener
        if (activity.isNetworkConnected().not()) {
            onShowAdCompleteListener.onShowAdComplete()
            return
        }
        if (appPreference.isPremium) {
            onShowAdCompleteListener.onShowAdComplete()
            return
        }
        if (interstitialAd?.isReady == true) {
            interstitialAd?.showAd()
        } else {
            loadAds(adId = adId)
            onShowAdCompleteListener.onShowAdComplete()
        }
    }

    fun resetAds() {
        interstitialAd = null
        countLoadAds = 0
    }

    override fun onAdLoaded(ad: MaxAd?) {
        countLoadAds = 0
    }

    override fun onAdDisplayed(ad: MaxAd?) {

    }

    override fun onAdHidden(ad: MaxAd?) {
        countLoadAds = 0
        appPreference.updateDelayAds()
        onShowAdCompleteListener?.onShowAdComplete()
        // Load ads tiếp theo
        interstitialAd?.loadAd()
    }

    override fun onAdClicked(ad: MaxAd?) {

    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        //load tối đa 3 lần. xịt rồi thì thôi.
        if (countLoadAds == 4) {
            resetAds()
            return
        }
        countLoadAds++
        // delay load ads theo 5 giây
        Handler(Looper.getMainLooper()).postDelayed({
            interstitialAd?.loadAd()
        }, 5000L)
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        interstitialAd?.loadAd()
    }

    override fun onAdRevenuePaid(ad: MaxAd?) {

    }

}

interface OnShowAdCompleteListener {
    fun onShowAdComplete(isFailed: Boolean = false)
}