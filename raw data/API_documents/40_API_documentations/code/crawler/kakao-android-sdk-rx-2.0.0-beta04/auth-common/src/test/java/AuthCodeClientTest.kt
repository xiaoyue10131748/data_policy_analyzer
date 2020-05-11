package com.kakao.sdk.auth

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AuthCodeClientTest {

    private val CLIENT_ID = "abcdefghijklmnopqrstuvwxyz"
    private val REDIRECT_URI = "kakao$CLIENT_ID://oauth"

    @Test
    fun syncExtension_notDisplay() {
        UriUtility.authorizeUri(
            clientId = CLIENT_ID,
            redirectUri = REDIRECT_URI,
            channelPublicIds = listOf(),
            serviceTerms = listOf()
        ).let {
            assert(value = it.getQueryParameter(Constants.CHANNEL_PUBLIC_ID) == "")
            assert(value = it.getQueryParameter(Constants.SERVICE_TERMS) == "")
        }
    }

    @Test
    fun syncExtension_multiple() {
        UriUtility.authorizeUri(
            clientId = CLIENT_ID,
            redirectUri = REDIRECT_URI,
            channelPublicIds = listOf("abc","efc"),
            serviceTerms = listOf("123","456")
        ).let {
            assert(value = it.getQueryParameter(Constants.CHANNEL_PUBLIC_ID) == "abc,efc")
            assert(value = it.getQueryParameter(Constants.SERVICE_TERMS) == "123,456")
        }
    }

    @Test
    fun syncExtension_autoLogin() {
        UriUtility.authorizeUri(
            clientId = CLIENT_ID,
            redirectUri = REDIRECT_URI,
            autoLogin = true
        ).let {
            assert(value = it.getQueryParameter(Constants.AUTO_LOGIN) == "true")
        }
    }
}