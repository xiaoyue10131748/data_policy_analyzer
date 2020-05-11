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
@file:JvmName("SdkLoggerKt")

package com.kakao.sdk.network

import com.kakao.sdk.common.*
import io.reactivex.subjects.PublishSubject

/**
 * @author kevin.kang. Created on 2019-10-16..
 */
class RxSdkLogger : SdkLogger() {

    private val subject = PublishSubject.create<Any>()
    val observable = subject.hide()

    /**
     * @suppress
     */
    override fun log(logged: Any, logLevel: LogLevel) {
        val loggedObject = "${logLevel.symbol} $logged"
        if (BuildConfig.DEBUG) {
            if (logLevel.level >= debugLogLevel.level) {
                subject.onNext(loggedObject)
            }
            return
        }
        if (KakaoSdk.loggingEnabled && logLevel.level >= releaseLogLevel.level) {
            subject.onNext(loggedObject)
        }
    }

    companion object {
        @JvmStatic
        val instance by lazy { SdkLogger.rx }
    }
}

val SdkLogger.Companion.rx by lazy { RxSdkLogger() }