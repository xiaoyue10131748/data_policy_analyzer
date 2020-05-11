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
package com.kakao.sdk.auth

import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.KakaoCustomTabsClient

/**
 * @suppress
 * @author kevin.kang. Created on 2018. 3. 24..
 */
class AuthCodeCustomTabsActivity : AppCompatActivity() {
    private lateinit var resultReceiver: ResultReceiver
    private lateinit var fullUri: Uri
    private var customTabsConnection: ServiceConnection? = null
    private var customTabsOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle =
            intent.extras?.getBundle(Constants.KEY_BUNDLE) ?: throw IllegalArgumentException()
        resultReceiver =
            bundle.getParcelable<ResultReceiver>(Constants.KEY_RESULT_RECEIVER) as ResultReceiver
        fullUri = bundle.getParcelable(Constants.KEY_FULL_URI) ?: throw IllegalArgumentException()
    }

    override fun onResume() {
        super.onResume()
        if (!customTabsOpened) {
            openChromeCustomTab(fullUri)
            customTabsOpened = true
        } else {
            resultReceiver.send(RESULT_CANCELED, Bundle().apply {
                putParcelable(
                    Constants.KEY_EXCEPTION,
                    ClientError(ClientErrorCause.Cancelled, "/oauth/authorize cancelled.")
                )
            })
            finish()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.dataString
        val bundle = Bundle()
        if (uri == null) {
            bundle.putParcelable(
                Constants.KEY_EXCEPTION,
                ClientError(ClientErrorCause.Cancelled, "/oauth/authorize cancelled.")
            )
            resultReceiver.send(RESULT_CANCELED, bundle)
            finish()
            return
        }
        bundle.putParcelable(Constants.KEY_URL, intent.data)
        resultReceiver.send(RESULT_OK, bundle)
        finish()
    }

    fun openChromeCustomTab(uri: Uri) {
        try {
            customTabsConnection = KakaoCustomTabsClient.openWithDefault(this, uri)
        } catch (e: UnsupportedOperationException) {
            KakaoCustomTabsClient.open(this, uri)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        customTabsConnection?.let { unbindService(it) }
    }
}