package vn.app.common_lib.helper

import android.content.Context
import android.content.SharedPreferences
import jp.takuji31.koreference.*
import kotlin.random.Random

class AppPreference private constructor(sharedPreferences: SharedPreferences) :
    KoreferenceModel(sharedPreferences) {

    private val dayRestartSaleOff by lazy {
        Random.nextInt(1, 3) * (24 * 60 * 60 * 1000)
    }

    constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences(
            context.packageName, Context.MODE_PRIVATE
        )
    )

    companion object {
        const val TIME_SALE_OFF = 1 * 60 * 60 * 1000
        const val MODE_THEME_NOT_CONFIG = -99
        private const val DAYS_RATE_DELAY = 2 * (24 * 60 * 60 * 1000)
        private const val FORCE_UPDATE_DAY_DELAY = 1 * (24 * 60 * 60 * 1000)
    }

    var isPremium: Boolean by booleanPreference(default = false)
    fun upgradePremium(premium: Boolean = true) {
        isPremium = premium
    }

    private var timeDelayShowAd by longPreference()
    fun updateDelayAds(timeDelay: Int = (60 * 1000)) {
        timeDelayShowAd = System.currentTimeMillis() + timeDelay
    }

    fun isTimeAdValid(): Boolean {
        return timeDelayShowAd < System.currentTimeMillis()
    }

    private var timeDelayShowRate: Long by longPreference(default = 0)
    var isNeverShowRate: Boolean by booleanPreference(default = false)
    fun updateShowRateDelay() {
        timeDelayShowRate = System.currentTimeMillis() + DAYS_RATE_DELAY
    }

    fun canShowDialogRate(): Boolean {
        return isNeverShowRate.not() && timeDelayShowRate < System.currentTimeMillis()
    }

    var timeLockProcess by longPreference(default = 5000L)

    var lastPathRenamed by stringPreference()

    var modeTheme by intPreference(default = MODE_THEME_NOT_CONFIG)
    private var appModeTime: Long by longPreference(default = -2L)

    fun blockShowMode() {
        appModeTime = -1L
    }

    fun canShowModeSpam(): Boolean {
        return appModeTime == -2L
    }

    var fileDataString by stringPreference()
    var filePathGetFromOtherApp by stringPreference()
    var uuidDownloadProcess: String by stringPreference()


    var currentLanguage: String by stringPreference(default = "")
    fun isSelectedLanguage(): Boolean {
        return currentLanguage.isNotBlank()
    }

    var isNeverAskLanguage by booleanPreference(default = false)
    fun canShowLanguage(): Boolean {
        return isNeverAskLanguage.not() && isPremium.not()
    }

    fun blockShowLanguageScreen() {
        isNeverAskLanguage = true
    }

    var isNeverAskChangeAuthor by booleanPreference(default = false)


    var timeSaleOff by longPreference()
    fun isFlashSale(): Boolean {
        return timeSaleOff > System.currentTimeMillis()
    }

    fun initSaleOff() {
        val isStart =
            timeSaleOff == 0L || (System.currentTimeMillis() > (timeSaleOff + dayRestartSaleOff))
        if (isStart) {
            timeSaleOff = System.currentTimeMillis() + TIME_SALE_OFF.toLong()
        }
    }

    var mustUpdateApp: Boolean by booleanPreference()
    var forceUpdateTime: Long by longPreference()
    fun updateForceTime() {
        forceUpdateTime = FORCE_UPDATE_DAY_DELAY + System.currentTimeMillis()
    }

    fun canShowForceUpdate(): Boolean {
        return forceUpdateTime < System.currentTimeMillis()
    }

    // true là show native, hide banner
    // false là hide native, show banner
    var showNativeOrHideBannerMain: Boolean by booleanPreference(default = false)

    fun showNativeMain(): Boolean {
        return showNativeOrHideBannerMain
    }

    fun showBannerMain(): Boolean {
        return showNativeOrHideBannerMain.not()
    }

}

