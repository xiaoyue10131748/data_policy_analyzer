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

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonObject
import com.kakao.sdk.auth.model.AccessTokenResponse
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoJson
import com.kakao.sdk.common.util.Cipher
import com.kakao.sdk.common.util.PersistentKVStore
import com.kakao.sdk.common.util.SharedPrefsWrapper
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

/**
 * @author kevin.kang. Created on 2018. 3. 28..
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DefaultAccessTokenRepoTest {
    lateinit var accessTokenRepo: DefaultAccessTokenRepo

    @Test
    fun toCache() {
        accessTokenRepo = DefaultAccessTokenRepo(
            getEmptyPreferences(),
            IdentityCipher()
        )
        var response = AccessTokenResponse(
            "new_test_access_token", "new_test_refresh_token",
            60 * 60, 60 * 60 * 12, "bearer"
        )
        accessTokenRepo.setToken(OAuthToken.fromResponse(response))
        response = AccessTokenResponse(
            accessToken = "new_test_access_token_2",
            accessTokenExpiresIn = 60 * 60 * 2,
            tokenType = "bearer"
        )
        accessTokenRepo.setToken(OAuthToken.fromResponse(response))
    }

    @Test
    fun fromEmptyCache() {
        accessTokenRepo = DefaultAccessTokenRepo(
            getEmptyPreferences(),
            IdentityCipher()
        )
    }

    @Test
    fun fromFullCache() {
        accessTokenRepo = DefaultAccessTokenRepo(
            getFullPreferences(),
            IdentityCipher()
        )
        val token = accessTokenRepo.getToken()
        val scopes = token.scopes
        assertNotNull(scopes)
        assertEquals(2, scopes!!.size)
        assertEquals("account_email", scopes[0])
        assertEquals("birthday", scopes[1])
    }

    @Test
    fun insecureToV2() {
        val cipher = SimpleCipher()
        val preferences = legacyPreferences()
        accessTokenRepo = DefaultAccessTokenRepo(preferences, cipher)
        val token = accessTokenRepo.getToken()
        assertEquals("test_access_token", token.accessToken)
        assertEquals("test_refresh_token", token.refreshToken)
    }

    @Test
    fun secureToV2() {
        val cipher = SimpleCipher()
        val preferences = legacyPreferences(cipher)
        accessTokenRepo = DefaultAccessTokenRepo(preferences, cipher)
        val token = accessTokenRepo.getToken()
        assertEquals("test_access_token", token.accessToken)
        assertEquals("test_refresh_token", token.refreshToken)

    }

    fun getEmptyPreferences(): PersistentKVStore {
        return SharedPrefsWrapper(
            ApplicationProvider.getApplicationContext<Application>().getSharedPreferences(
                "test_app_key",
                Context.MODE_PRIVATE
            )
        )
    }

    fun getFullPreferences(cipher: Cipher = IdentityCipher()): PersistentKVStore {
        val preferences = ApplicationProvider.getApplicationContext<Application>()
            .getSharedPreferences("test_app_key", Context.MODE_PRIVATE)

        val token = OAuthToken(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token",
            accessTokenExpiresAt = Date(Date().time + 1000L * 60 * 60 * 12),
            refreshTokenExpiresAt = Date(Date().time + 1000L * 60 * 60 * 24 * 30),
            scopes = listOf("account_email", "birthday")
        )
        preferences.edit()
            .putString(DefaultAccessTokenRepo.tokenKey, cipher.encrypt(KakaoJson.toJson(token)))
            .putString(DefaultAccessTokenRepo.versionKey, "2.0.0")
            .commit()
        return SharedPrefsWrapper(preferences)
    }

    fun legacyPreferences(cipher: Cipher? = null): PersistentKVStore {
        val preferences = ApplicationProvider.getApplicationContext<Application>()
            .getSharedPreferences("test_app_key", Context.MODE_PRIVATE)

        val legacyAt = JsonObject()
        legacyAt.addProperty("value", cipher?.encrypt("test_access_token") ?: "test_access_token")
        legacyAt.addProperty("valueType", "string")
        val legacyRt = JsonObject()
        legacyRt.addProperty("value", cipher?.encrypt("test_refresh_token") ?: "test_refresh_token")
        legacyRt.addProperty("valueType", "string")
        val legacyAtExpiresAt = JsonObject()
        legacyAtExpiresAt.addProperty("value", 1555099407890L)
        legacyAtExpiresAt.addProperty("valueType", "long")
        val legacyRtExpiresAt = JsonObject()
        legacyRtExpiresAt.addProperty("value", 1557648207890L)
        legacyRtExpiresAt.addProperty("valueType", "long")

        val secureMode = JsonObject()
        secureMode.addProperty("value", cipher != null)
        secureMode.addProperty("valueType", "string")
        preferences.edit().putString(DefaultAccessTokenRepo.atKey, legacyAt.toString())
            .putString(DefaultAccessTokenRepo.rtKey, legacyRt.toString())
            .putString(DefaultAccessTokenRepo.atExpiresAtKey, legacyAtExpiresAt.toString())
            .putString(DefaultAccessTokenRepo.rtExpiresAtKey, legacyRtExpiresAt.toString())
            .putString(
                DefaultAccessTokenRepo.scopesKey,
                listOf("account_email", "birthday").joinToString(" ")
            )
            .putString(DefaultAccessTokenRepo.secureModeKey, secureMode.toString()).commit()
        return SharedPrefsWrapper(preferences)
    }
}