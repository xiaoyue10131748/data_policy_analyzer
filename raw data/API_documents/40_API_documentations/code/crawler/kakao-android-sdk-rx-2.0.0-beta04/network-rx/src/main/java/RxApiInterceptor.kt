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
package com.kakao.sdk.network

import com.kakao.sdk.common.model.ApiError
import com.kakao.sdk.common.model.ApiErrorCause
import com.kakao.sdk.common.KakaoJson
import com.kakao.sdk.common.SdkLogger
import com.kakao.sdk.common.model.ApiErrorResponse
import io.reactivex.Completable
import io.reactivex.CompletableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer
import retrofit2.HttpException

/**
 * @author kevin.kang. Created on 2019-10-17..
 */
object RxApiInterceptor {

    fun <T> handleApiError(): SingleTransformer<T, T> {
        return SingleTransformer { it ->
            it.onErrorResumeNext { Single.error(translateError(it)) }
                .doOnError { SdkLogger.rx.e(it) }
                .doOnSuccess { SdkLogger.rx.i(it.toString()) }
        }
    }

    fun handleCompletableError(): CompletableTransformer {
        return CompletableTransformer {
            it.onErrorResumeNext { Completable.error(translateError(it)) }
                .doOnError { SdkLogger.rx.e(it) }
                .doOnComplete { }
        }
    }

    @JvmSynthetic
    internal fun translateError(t: Throwable): Throwable {
        try {
            if (t is HttpException) {
                val errorString = t.response()?.errorBody()?.string()
                val response =
                    KakaoJson.fromJson<ApiErrorResponse>(
                        errorString!!,
                        ApiErrorResponse::class.java
                    )
                val cause =
                    KakaoJson.fromJson(
                        response.code.toString(),
                        ApiErrorCause::class.java
                    )
                        ?: ApiErrorCause.Unknown
                return ApiError(t.code(), cause, response)
            }
            return t
        } catch (unexpected: Throwable) {
            return unexpected
        }
    }
}