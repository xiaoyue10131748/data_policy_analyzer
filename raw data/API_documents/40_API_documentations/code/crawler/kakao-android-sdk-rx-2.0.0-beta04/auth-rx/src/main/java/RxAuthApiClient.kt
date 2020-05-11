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

import com.kakao.sdk.auth.model.AccessTokenResponse
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.AgtResponse
import com.kakao.sdk.auth.network.kauth
import com.kakao.sdk.common.ApiFactory
import com.kakao.sdk.common.KakaoJson
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.SdkLogger
import com.kakao.sdk.common.model.AuthErrorResponse
import com.kakao.sdk.common.model.OAuthError
import com.kakao.sdk.network.rx
import io.reactivex.Single
import io.reactivex.SingleTransformer
import retrofit2.HttpException

/**
 * 카카오 OAuth 서버에서 제공하는 API 를 사용하기 위한 클라이언트.
 *
 * @author kevin.kang. Created on 2018. 3. 28..
 */
class RxAuthApiClient(
    private val authApi: RxAuthApi = ApiFactory.kauth.create(RxAuthApi::class.java),
    private val accessTokenRepo: AccessTokenRepo = AccessTokenRepo.instance
) {

    /**
     * [RxAuthCodeClient] 를 이용하여 발급 받은 authorization code 를 사용하여 액세스 토큰을 발급한다.
     *
     * @param authCode authorization code
     *
     * @return [Single] instance that will emit [AccessTokenResponse].
     */
    @JvmOverloads
    fun issueAccessToken(
        authCode: String,
        redirectUri: String = "kakao${KakaoSdk.applicationContextInfo.appKey}://oauth",
        clientId: String = KakaoSdk.applicationContextInfo.appKey,
        androidKeyHash: String = KakaoSdk.applicationContextInfo.signingKeyHash
    ): Single<AccessTokenResponse> =
        authApi.issueAccessToken(
            clientId = clientId,
            redirectUri = redirectUri,
            androidKeyHash = androidKeyHash,
            authCode = authCode
        ).compose(handleAuthError())
            .doOnSuccess { accessTokenRepo.setToken(OAuthToken.fromResponse(it)) }


    /**
     * 기존에 [issueAccessToken] 또는 이 메소드를 사용하여 발급 받은 리프레시 토큰으로 액세스 토큰을 갱신한다.
     *
     * @param refreshToken 리프레시 토큰
     *
     * @return [Single] instance that will emit [AccessTokenResponse]
     */
    @JvmOverloads
    fun refreshAccessToken(
        refreshToken: String,
        clientId: String = KakaoSdk.applicationContextInfo.appKey,
        androidKeyHash: String = KakaoSdk.applicationContextInfo.signingKeyHash
    ): Single<AccessTokenResponse> =
        authApi.issueAccessToken(
            clientId = clientId,
            redirectUri = null,
            androidKeyHash = androidKeyHash,
            refreshToken = refreshToken,
            grantType = Constants.REFRESH_TOKEN
        ).compose(handleAuthError())
            .doOnSuccess { accessTokenRepo.setToken(OAuthToken.fromResponse(it)) }


    /**
     * @suppress
     */
    fun agt(clientId: String = KakaoSdk.applicationContextInfo.appKey): Single<AgtResponse> =
        Single.just(accessTokenRepo.getToken().accessToken)
            .flatMap { authApi.agt(clientId, it) }
            .compose(handleAuthError())

    @JvmSynthetic
    internal fun <T> handleAuthError(): SingleTransformer<T, T> = SingleTransformer {
        it.onErrorResumeNext { Single.error(translateError(it)) }
            .doOnError { SdkLogger.rx.e(it) }
            .doOnSuccess { SdkLogger.rx.i(it.toString()) }
    }

    @JvmSynthetic
    internal fun translateError(t: Throwable): Throwable {
        if (t is HttpException) {
            val errorString = t.response()?.errorBody()?.string()
            val response =
                KakaoJson.fromJson<AuthErrorResponse>(errorString!!, AuthErrorResponse::class.java)
            val cause = KakaoJson.fromJson(response.error, AuthErrorCause::class.java)
                ?: AuthErrorCause.Unknown
            return OAuthError(t.code(), cause, response)
        }
        return t
    }
}

/**
 * Rx 용 AuthApiClient 싱긅을 접근하기 위한 extension property
 */
val AuthApiClient.Companion.rx by lazy { RxAuthApiClient() }