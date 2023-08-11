package vn.app.common

import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import vn.app.common_lib.helper.AppPreference
import vn.app.common_lib.helper.JsonHelper
import java.util.concurrent.TimeUnit

val apiModules = module {
    single { JsonHelper.gson }

    single {
        val builder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

//        if (BuildConfig.DEBUG) {
//            builder.attachToDebugger()
//        }

        //builder.addInterceptor(UpdateProcessInterceptor())
        builder.cache(null)
        return@single builder.build()
    }
}

val appModules = module {
    single { androidApplication() as MyApplication }
}

val dataModules = module {
    single { AppPreference(get()) }
}

val diModules = listOf(
    appModules,
    dataModules,
    apiModules
)
