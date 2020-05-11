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

import com.kakao.sdk.auth.model.OAuthToken

/**
 * 카카오 API 에 사용되는 액세스 토큰, 리프레시 토큰을 관리하는 저장소.
 *
 * SDK v2 에서 유저의 로그인 여부를 판단하는데 쓰입니다.
 *
 * @since 2.0.0
 *
 * @author kevin.kang. Created on 2018. 3. 24..
 */
interface AccessTokenRepo {
    /**
     * 앱캐시에 저장되어 있는 [OAuthToken] 객체를 리턴.
     *
     * 아래와 같이 현재 유저의 로그인 여부를 확인할 수 있습니다.
     *
     * ```kotlin
     * if (AccessTokenRepo.fromCache().refreshToken != null) {
     *   // 리프레시토큰을 통해 액세스토큰을 갱신할 수 있는 상태
     * } else {
     *   // 리프레시토큰이 존재하지 않으므로 액세스토큰을 갱신할 수 없고 재로그인이 필요함.
     * }
     * ```
     * @return OAuthToken instance stored in the cache
     * @since 2.0.0
     */
    fun getToken(): OAuthToken

    /**
     * 새로운 토큰으로 캐시를 업데이트 함. 파라미터로 제공된 토큰이 아닌 새로운 토큰이 생성됨.
     *
     * @param token [OAuthToken] from /oauth/token API
     * @return updated [OAuthToken] instance
     *
     * @since 2.0.0
     *
     * @see [OAuthToken.fromResponse]
     */
    fun setToken(token: OAuthToken): OAuthToken

    /**
     * 캐시에 저장되어 있는 [OAuthToken] 객체를 지움.
     *
     * Clear [OAuthToken] in cache used by the SDK
     *
     * @since 2.0.0
     */
    fun clear()

    companion object {
        /**
         * 기본적으로 SDK 에서 제공되는 AccessTokenRepo singleton 객체
         *
         * @since 2.0.0
         */
        @JvmStatic
        val instance: AccessTokenRepo by lazy {
            DefaultAccessTokenRepo()
        }
    }
}