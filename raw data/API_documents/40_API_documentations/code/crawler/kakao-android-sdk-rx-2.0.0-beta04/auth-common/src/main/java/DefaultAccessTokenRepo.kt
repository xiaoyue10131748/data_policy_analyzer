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

import com.google.gson.JsonObject
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoJson
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.SdkLogger
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.AESCipher
import com.kakao.sdk.common.util.Cipher
import com.kakao.sdk.common.util.PersistentKVStore
import com.kakao.sdk.common.util.SharedPrefsWrapper
import com.kakao.sdk.network.rx
import java.util.*
import kotlin.Exception

/**
 * @suppress
 * @author kevin.kang. Created on 2018. 3. 27..
 */
class DefaultAccessTokenRepo(
    val appCache: PersistentKVStore = SharedPrefsWrapper(
        KakaoSdk.applicationContextInfo.sharedPreferences
    ),
    val encryptor: Cipher = AESCipher()
) : AccessTokenRepo {
    private var currentToken: OAuthToken

    init {
        val version = appCache.getString(versionKey)
        if (version == null) {
            migrateFromOldVersion()
        }
        val serialized = appCache.getString(tokenKey)
        currentToken = if (serialized == null) {
            OAuthToken()
        } else {
            try {
                KakaoJson.fromJson<OAuthToken>(
                    encryptor.decrypt(serialized),
                    OAuthToken::class.java
                )
            } catch (e: Throwable) {
                SdkLogger.rx.e(
                    ClientError(
                        ClientErrorCause.TokenNotFound,
                        "Token decryption failed: $e"
                    )
                )
                OAuthToken()
            }
        }
        appCache.putString(versionKey, BuildConfig.VERSION_NAME).commit()
    }

    override fun clear() {
        currentToken = OAuthToken()
        appCache.remove(tokenKey).commit()
    }

    override fun setToken(token: OAuthToken): OAuthToken {
        val newToken = OAuthToken(
            accessToken = token.accessToken,
            accessTokenExpiresAt = token.accessTokenExpiresAt,
            refreshToken = token.refreshToken ?: currentToken.refreshToken,
            refreshTokenExpiresAt = token.refreshTokenExpiresAt
                ?: currentToken.refreshTokenExpiresAt,
            scopes = token.scopes ?: currentToken.scopes
        )
        try {
            appCache.putString(tokenKey, encryptor.encrypt(KakaoJson.toJson(newToken))).commit()
        } catch (e: Throwable) {
            SdkLogger.rx.e(
                ClientError(
                    ClientErrorCause.TokenNotFound,
                    "Token encryption failed: $e"
                )
            )
        }
        currentToken = newToken
        return newToken
    }

    override fun getToken(): OAuthToken {
        return currentToken
    }

    companion object {
        const val atKey = "com.kakao.token.AccessToken"
        const val rtKey = "com.kakao.token.RefreshToken"
        const val atExpiresAtKey = "com.kakao.token.OAuthToken.ExpiresAt"
        const val rtExpiresAtKey = "com.kakao.token.RefreshToken.ExpiresAt"
        const val secureModeKey = "com.kakao.token.KakaoSecureMode"
        const val scopesKey = "com.kakao.token.Scopes"

        const val tokenKey = "com.kakao.sdk.oauth_token"
        const val versionKey = "com.kakao.sdk.version"
    }

    private fun migrateFromOldVersion() {
        val secureMode = appCache.getString(secureModeKey, null)?.let {
            KakaoJson.fromJson<JsonObject>(it, JsonObject::class.java)["value"].asString
        } ?: "false"
        val at = parseOrNull {
            appCache.getString(atKey, null)?.let {
                val legacyAt =
                    KakaoJson.fromJson<JsonObject>(
                        it,
                        JsonObject::class.java
                    )["value"].asString
                if (legacyAt != null && secureMode == "true") encryptor.decrypt(legacyAt) else legacyAt
            }
        }
        val rt = parseOrNull {
            appCache.getString(rtKey, null)?.let {
                val legacyRt =
                    KakaoJson.fromJson<JsonObject>(
                        it,
                        JsonObject::class.java
                    )["value"].asString
                if (legacyRt != null && secureMode == "true") encryptor.decrypt(legacyRt) else legacyRt
            }
        }
        val atExpiresAt = parseOrNull {
            appCache.getString(atExpiresAtKey, null)?.let {
                KakaoJson.fromJson<JsonObject>(it, JsonObject::class.java)["value"].asLong
            }
        } ?: 0L
        val rtExpiresAt = parseOrNull {
            appCache.getString(rtExpiresAtKey, null)?.let {
                KakaoJson.fromJson<JsonObject>(it, JsonObject::class.java)["value"].asLong
            }
        } ?: Long.MAX_VALUE
        val token = OAuthToken(
            accessToken = at,
            accessTokenExpiresAt = Date(atExpiresAt),
            refreshToken = rt,
            refreshTokenExpiresAt = Date(rtExpiresAt)
        )
        appCache.putString(tokenKey, encryptor.encrypt(KakaoJson.toJson(token)))
            .remove(secureModeKey).remove(atKey).remove(rtKey).remove(atExpiresAtKey)
            .remove(rtExpiresAtKey).remove(scopesKey)
            .putString(versionKey, BuildConfig.VERSION_NAME)
            .commit()
    }
}

private inline fun <T> parseOrNull(f: () -> T): T? =
    try {
        f()
    } catch (e: Exception) {
        null
    }