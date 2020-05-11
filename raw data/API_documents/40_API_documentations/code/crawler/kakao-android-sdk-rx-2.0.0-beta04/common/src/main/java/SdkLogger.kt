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

/**
 * @author kevin.kang. Created on 2019-10-31..
 */
abstract class SdkLogger {
    var debugLogLevel = LogLevel.V
    var releaseLogLevel = LogLevel.I

    fun v(logged: Any) = log(logged, LogLevel.V)

    fun d(logged: Any) = log(logged, LogLevel.D)

    fun i(logged: Any) = log(logged, LogLevel.I)

    fun w(logged: Any) = log(logged, LogLevel.W)

    fun e(logged: Any) = log(logged, LogLevel.E)

    /**
     * @suppress
     */
    abstract fun log(logged: Any, logLevel: LogLevel)

    companion object
}

enum class LogLevel(val level: Int, val symbol: String) {
    V(0, "[\uD83D\uDCAC]"),
    D(1, "[ℹ️]"),
    I(2, "[\uD83D\uDD2C]"),
    W(3, "[⚠️]"),
    E(4, "[‼️]")
}