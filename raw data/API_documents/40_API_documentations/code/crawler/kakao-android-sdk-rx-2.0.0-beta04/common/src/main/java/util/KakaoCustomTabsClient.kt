/*
  Copyright 2019 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.sdk.common.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection

/**
 * @author kevin.kang. Created on 01/04/2019..
 */
object KakaoCustomTabsClient {

    @Throws(UnsupportedOperationException::class)
    fun openWithDefault(context: Context, uri: Uri): ServiceConnection? {
        val packageName = resolveCustomTabsPackage(
            context,
            uri
        ) ?: throw UnsupportedOperationException()
        Log.d("KakaoCustomTabsClient", "Choosing $packageName as custom tabs browser")
        val connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName?, client: CustomTabsClient?) {
                val builder = CustomTabsIntent.Builder()
                        .enableUrlBarHiding().setShowTitle(true)
                val customTabsIntent = builder.build()
                customTabsIntent.intent.data = uri
                customTabsIntent.intent.setPackage(packageName)
                context.startActivity(customTabsIntent.intent)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d("KakaoCustomTabsClient", "onServiceDisconnected: $name")
            }
        }
        val bound = CustomTabsClient.bindCustomTabsService(context, packageName, connection)
        return if (bound) connection else null
    }

    fun open(context: Context, uri: Uri) {
        CustomTabsIntent.Builder().enableUrlBarHiding().setShowTitle(true).build()
                .launchUrl(context, uri)
    }

    fun resolveCustomTabsPackage(context: Context, uri: Uri): String? {
        var packageName: String? = null
        var chromePackage: String? = null
        // get ResolveInfo for default browser
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val serviceIntent = Intent().setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION)
        val serviceInfos = context.packageManager.queryIntentServices(serviceIntent, 0)
        for (info in serviceInfos) {
            // check if chrome is available on this device
            if (chromePackage == null && isPackageNameChrome(
                    info.serviceInfo.packageName
                )
            ) {
                chromePackage = info.serviceInfo.packageName
            }
            // check if the browser being looped is the default browser
            if (info.serviceInfo.packageName == resolveInfo.activityInfo.packageName) {
                packageName = resolveInfo.activityInfo.packageName
                break
            }
        }
        if (packageName == null && chromePackage != null) {
            packageName = chromePackage
        }
        return packageName
    }

    private fun isPackageNameChrome(packageName: String): Boolean {
        return chromePackageNames.contains(packageName)
    }

    private val chromePackageNames = arrayOf(
            "com.android.chrome",
            "com.chrome.beta",
            "com.chrome.dev")
}