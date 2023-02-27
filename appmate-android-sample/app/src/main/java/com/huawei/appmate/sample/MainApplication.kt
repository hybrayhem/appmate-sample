package com.huawei.appmate.sample;

import android.app.Application
import com.huawei.appmate.PurchaseClient

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PurchaseClient.getInstance(this, BuildConfig.APPMATE_KEY) // Initialize Appmate SDK, Key stored in local.properties
    }
}
