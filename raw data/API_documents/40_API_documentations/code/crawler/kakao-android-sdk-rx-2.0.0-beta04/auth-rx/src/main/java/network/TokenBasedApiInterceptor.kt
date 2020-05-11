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

import android.content.Context
import com.kakao.sdk.auth.*
import com.kakao.sdk.auth.model.AccessTokenResponse
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.*
import com.kakao.sdk.common.model.ApiError
import com.kakao.sdk.common.model.ApiErrorCause
import io.reactivex.*
import com.kakao.sdk.common.model.ApplicationInfo
import com.kakao.sdk.common.model.ContextInfo
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher

/**
 * @author kevin.kang. Created on 2018. 5. 2..
 */
class TokenBasedApiInterceptor(
    private val authApiClient: RxAuthApiClient = AuthApiClient.rx,
    private val accessTokenRepo: AccessTokenRepo = AccessTokenRepo.instance,
    private val appInfo: ApplicationInfo = KakaoSdk.applicationContextInfo,
    private val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo
) {
    fun <T> handleApiError(): SingleTransformer<T, T> =
        SingleTransformer { it ->
            it.retryWhen { refreshAccessToken(it) }
                .doOnError {
                    if (it is ApiError && it.reason == ApiErrorCause.InvalidToken)
                        accessTokenRepo.clear()
                }
        }


    fun handleCompletableError(): CompletableTransformer =
        CompletableTransformer {
            it.retryWhen { refreshAccessToken(it) }
                .doOnError {
                    if (it is ApiError && it.reason == ApiErrorCause.InvalidToken)
                        accessTokenRepo.clear()
                }
        }

    fun dynamicAgreement(context: Context) = { flowable: Flowable<Throwable> ->
        flowable.flatMap {
            if (it is ApiError && it.reason == ApiErrorCause.InvalidScope) {
                return@flatMap AuthCodeClient.rx.authorizeWithNewScopes(
                    context,
                    it.response.requiredScopes!!
                ).observeOn(Schedulers.io())
                    .flatMap { authApiClient.issueAccessToken(it) }.toFlowable()
            }
            return@flatMap Flowable.error<OAuthToken>(it)
        }
    }

    @JvmSynthetic
    internal fun refreshAccessToken(throwableFlowable: Flowable<Throwable>): Publisher<AccessTokenResponse> =
        throwableFlowable.take(3).flatMap {
            val token = accessTokenRepo.getToken()
            if (token.refreshToken == null || it !is ApiError || it.reason != ApiErrorCause.InvalidToken) {
                throw it
            }
            return@flatMap authApiClient.refreshAccessToken(
                token.refreshToken!!,
                appInfo.appKey,
                contextInfo.signingKeyHash
            ).toFlowable()
        }

    companion object {
        val instance by lazy {
            TokenBasedApiInterceptor()
        }
    }
}