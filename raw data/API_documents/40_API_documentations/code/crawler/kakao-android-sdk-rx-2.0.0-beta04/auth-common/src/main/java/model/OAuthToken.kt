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
package com.kakao.sdk.auth.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

/**
 * 카카오 로그인을 통해 발급 받은 토큰입니다.
 *
 * @property refreshToken 액세스토큰을 갱신할 수 있는 리프레시토큰입니다.
 *
 * @author kevin.kang. Created on 2018. 3. 20..
 */
@Parcelize
data class OAuthToken(
    val accessToken: String? = null,
    val accessTokenExpiresAt: Date? = null,
    val refreshToken: String? = null,
    val refreshTokenExpiresAt: Date? = null,
    val scopes: List<String>? = null
) : Parcelable {

    companion object {

        /**
         * [AccessTokenResponse] 객체로부터 OAuthToken 객체를 생성합니다.
         */
        fun fromResponse(response: AccessTokenResponse): OAuthToken =
            OAuthToken(
                accessToken = response.accessToken,
                accessTokenExpiresAt = Date(Date().time + 1000L * response.accessTokenExpiresIn),
                refreshToken = response.refreshToken,
                refreshTokenExpiresAt =
                response.refreshTokenExpiresIn?.let { Date(Date().time + 1000L * it) },
                scopes = response.scopes?.split(" ")
            )
    }
}