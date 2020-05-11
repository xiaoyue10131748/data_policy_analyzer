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

import android.net.Uri
import com.kakao.sdk.common.KakaoSdk

/**
 * @suppress
 */
object UriUtility {

    fun authorizeUri(
        clientId: String,
        agt: String? = null,
        redirectUri: String,
        scopes: List<String>? = null,
        kaHeader: String? = null,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        autoLogin: Boolean? = null
    ): Uri {
        val builder = Uri.Builder()
            .scheme(com.kakao.sdk.network.Constants.SCHEME)
            .authority(KakaoSdk.serverHosts.kauth).path(Constants.AUTHORIZE_PATH)
            .appendQueryParameter(Constants.CLIENT_ID, clientId)
            .appendQueryParameter(Constants.REDIRECT_URI, redirectUri)
            .appendQueryParameter(Constants.RESPONSE_TYPE, Constants.CODE).apply {
                agt?.let { appendQueryParameter(Constants.AGT, agt) }
                if (!scopes.isNullOrEmpty()) {
                    appendQueryParameter(Constants.SCOPE, scopes.joinToString(","))
                }
                channelPublicIds?.let { appendQueryParameter(Constants.CHANNEL_PUBLIC_ID, channelPublicIds.joinToString(",")) }
                serviceTerms?.let { appendQueryParameter(Constants.SERVICE_TERMS, serviceTerms.joinToString(",")) }
                autoLogin?.let { appendQueryParameter("auto_login", it.toString()) }
            }
            .appendQueryParameter(Constants.KA_HEADER, kaHeader);
        return builder.build()
    }
}