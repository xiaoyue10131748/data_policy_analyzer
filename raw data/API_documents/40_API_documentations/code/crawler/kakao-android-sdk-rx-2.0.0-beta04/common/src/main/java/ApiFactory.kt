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
package com.kakao.sdk.common

import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @suppress
 * @author kevin.kang. Created on 2019-10-31..
 */
object ApiFactory {
    fun withClientAndAdapter(
        url: String,
        clientBuilder: OkHttpClient.Builder,
        factory: CallAdapter.Factory
    ): Retrofit =
        Retrofit.Builder().baseUrl(url)
            .addCallAdapterFactory(factory)
            .addConverterFactory(KakaoRetrofitConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(KakaoJson.base))
            .client(clientBuilder.build())
            .build()
}