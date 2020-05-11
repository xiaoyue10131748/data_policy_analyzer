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
package com.kakao.sdk.user.model

import com.google.gson.JsonObject
import com.kakao.sdk.common.KakaoJson
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.Constants
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * @author kevin.kang. Created on 2018. 4. 25..
 */
class UserTest {
    @ValueSource(strings = ["only_email", "only_phone", "preregi"])
    @ParameterizedTest
    fun parse(path: String) {
        val body = Utility.getJson("json/users/$path.json")
        val expected = KakaoJson.fromJson<JsonObject>(body, JsonObject::class.java)
        val response = KakaoJson.fromJson<User>(body, User::class.java)

        assertEquals(expected[Constants.ID].asLong, response.id)
        if (expected.has(Constants.PROPERTIES)) {
            assertEquals(
                expected[Constants.PROPERTIES].asJsonObject.keySet().size,
                response.properties?.size
            )
        }

        if (expected.has(Constants.KAKAO_ACCOUNT)) {
            assertNotNull(response.kakaoAccount)

            val expectedAccount = expected[Constants.KAKAO_ACCOUNT].asJsonObject
            val account = response.kakaoAccount
            assertEquals(
                expectedAccount[Constants.EMAIL_NEEDS_AGREEMENT]?.asBoolean,
                account?.emailNeedsAgreement
            )
            assertEquals(
                expectedAccount[Constants.IS_EMAIL_VERIFIED]?.asBoolean,
                account?.isEmailVerified
            )
            assertEquals(
                expectedAccount[Constants.IS_EMAIL_VALID]?.asBoolean,
                account?.isEmailValid
            )
            assertEquals(
                expectedAccount[Constants.EMAIL]?.asString,
                account?.email
            )
        }
    }

    @Test
    fun ageRange() {
        val first = KakaoJson.fromJson<AgeRange>("0~9", AgeRange::class.java)
        assertEquals(AgeRange.AGE_0_9, first)
        val last = KakaoJson.fromJson<AgeRange>("90~", AgeRange::class.java)
        assertEquals(AgeRange.AGE_90_ABOVE, last)
        val unknown = KakaoJson.fromJson<AgeRange>("unknown", AgeRange::class.java)
        assertEquals(AgeRange.UNKNOWN, unknown)
    }
}