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
import com.kakao.sdk.auth.network.AccessTokenInterceptor
import com.kakao.sdk.network.Constants
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

import org.junit.Assert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author kevin.kang. Created on 2018. 3. 30..
 */
class AccessTokenInterceptorTest {
    private lateinit var interceptor: AccessTokenInterceptor

    @BeforeEach
    fun setup() {

        val repo = object : AccessTokenRepo {
            override fun getToken(): OAuthToken =
                OAuthToken("access_token")

            override fun setToken(token: OAuthToken): OAuthToken {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun clear() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        interceptor = AccessTokenInterceptor(repo)
    }

    @Test
    fun interceptor() {
        val client = OkHttpClient().newBuilder().addInterceptor(interceptor).build()
        val server = MockWebServer()
        server.start()

        server.enqueue(MockResponse())
        client.newCall(Request.Builder().url(server.url("/")).build()).execute()
        val request = server.takeRequest()

        assertEquals("${Constants.BEARER} access_token", request.getHeader(Constants.AUTHORIZATION))
        server.shutdown()
    }
}