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
package com.kakao.sdk.auth.network

import com.kakao.sdk.common.ApiFactory
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.network.Constants
import com.kakao.sdk.common.AppKeyInterceptor
import com.kakao.sdk.common.KakaoAgentInterceptor
import com.kakao.sdk.network.loggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * @author kevin.kang. Created on 2018. 3. 23..
 */

val ApiFactory.kapiWithOAuth by lazy {
    ApiFactory.withClientAndAdapter(
        "${Constants.SCHEME}://${KakaoSdk.serverHosts.kapi}",
        OkHttpClient.Builder()
            .addInterceptor(KakaoAgentInterceptor())
            .addInterceptor(AccessTokenInterceptor())
            .addInterceptor(ApiFactory.loggingInterceptor),
        RxJava2CallAdapterFactory.create()
    )
}

val ApiFactory.kauth by lazy {
    ApiFactory.withClientAndAdapter(
        "${Constants.SCHEME}://${KakaoSdk.serverHosts.kauth}",
        OkHttpClient.Builder()
            .addInterceptor(KakaoAgentInterceptor())
            .addInterceptor(AppKeyInterceptor())
            .addInterceptor(ApiFactory.loggingInterceptor),
        RxJava2CallAdapterFactory.create()
    )
}