package vn.app.common_lib.google.admob

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import vn.app.common_lib.google.MySingletonHolder
import vn.app.common_lib.helper.AppPreference
import vn.app.common_lib.helper.isNetworkConnected

interface ShowAdListener {
    fun onShowAdComplete()
    fun onShowAdFailed() {}
}

class CMAInterstitialUtil(
    private val context: Context,
    private val appPreference: AppPreference
) {

    private var isReloaded: Boolean = false

    private var interstitialAd: InterstitialAd? = null

    companion object :
        MySingletonHolder<CMAInterstitialUtil, Context, AppPreference>(::CMAInterstitialUtil)

    fun loadAd(adUnitId: String) {
        if (context.isNetworkConnected().not()) {
            return
        }
        if (appPreference.isPremium.not()) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        if (isReloaded.not()) {
                            isReloaded = true
                            loadAd(adUnitId)
                        }
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                    }
                }
            )
        }
    }

    fun interstitialAdOk(): Boolean {
        return interstitialAd != null && isReloaded
    }

    fun resetAds() {
        interstitialAd = null
        isReloaded = false
    }

    fun forceShowInterstitial(
        activity: Activity,
        adUnitIdNext: String,
        onShowAdListener: ShowAdListener
    ) {
        if (appPreference.isPremium) {
            onShowAdListener.onShowAdComplete()
            return
        }

        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    interstitialAd = null
                    appPreference.updateDelayAds(timeDelay = 24 * 1000)
                    loadAd(adUnitIdNext)
                    onShowAdListener.onShowAdComplete()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    interstitialAd = null
                    onShowAdListener.onShowAdFailed()
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is dismissed.
                    isReloaded = true
                }
            }

            interstitialAd?.show(activity)
        } else {
            onShowAdListener.onShowAdFailed()
        }
    }

    fun showInterstitial(
        activity: Activity,
        adUnitIdNext: String,
        onShowAdCompleteListener: ShowAdListener
    ) {
        if (appPreference.isPremium) {
            onShowAdCompleteListener.onShowAdComplete()
            return
        }

        if (interstitialAd != null && appPreference.isTimeAdValid()) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    interstitialAd = null
                    loadAd(adUnitIdNext)
                    appPreference.updateDelayAds()
                    onShowAdCompleteListener.onShowAdComplete()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    interstitialAd = null
                    onShowAdCompleteListener.onShowAdFailed()
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is dismissed.
                    isReloaded = true
                }
            }
            interstitialAd?.show(activity)
        } else {
            onShowAdCompleteListener.onShowAdFailed()
        }
    }
}