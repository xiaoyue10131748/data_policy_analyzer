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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import com.kakao.sdk.common.KakaoJson
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.SdkLogger
import com.kakao.sdk.common.model.*
import com.kakao.sdk.common.util.IntentResolveClient
import com.kakao.sdk.network.rx
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleTransformer
import java.net.HttpURLConnection

/**
 * OAuth 2.0 Spec 의 authorization code 를 발급받기 위해 사용되는 클라이언트.
 *
 * @author kevin.kang. Created on 2018. 3. 20..
 */
class RxAuthCodeClient(
    private val authApiClient: RxAuthApiClient = AuthApiClient.rx,
    private val intentResolveClient: IntentResolveClient = IntentResolveClient.instance
) {

    private val CAPRI_LOGGED_IN_ACTIVITY = "com.kakao.talk.intent.action.CAPRI_LOGGED_IN_ACTIVITY"
    /**
     * 카카오톡 간편 로그인을 통하여 authorization code 를 발급 받는다.
     */
    @JvmOverloads
    fun authorizeWithTalk(
        context: Context,
        requestCode: Int,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        autoLogin: Boolean? = null,
        redirectUri: String = "kakao${KakaoSdk.applicationContextInfo.appKey}://oauth",
        clientId: String = KakaoSdk.applicationContextInfo.appKey,
        kaHeader: String = KakaoSdk.applicationContextInfo.kaHeader
    ): Single<String> =
        Single.create<String> {
            val extras = Bundle().apply {
                channelPublicIds?.let { putString(Constants.CHANNEL_PUBLIC_ID, channelPublicIds.joinToString(",")) }
                serviceTerms?.let { putString(Constants.SERVICE_TERMS, serviceTerms.joinToString(",")) }
                autoLogin?.let { putString(Constants.AUTO_LOGIN, it.toString()) }
            }
            val talkIntent = talkLoginIntent(clientId, redirectUri, kaHeader, extras)
            val resolvedIntent = intentResolveClient.resolveTalkIntent(context, talkIntent)
            if (resolvedIntent == null) {
                it.onError(
                    ClientError(
                        ClientErrorCause.NotSupported,
                        "KakaoTalk not installed"
                    )
                )
                return@create
            }
            context.startActivity(
                Intent(context, TalkAuthCodeActivity::class.java)
                    .putExtra(
                        Constants.KEY_LOGIN_INTENT,
                        intentResolveClient.resolveTalkIntent(context, talkIntent)
                    )
                    .putExtra(Constants.KEY_REQUEST_CODE, requestCode)
                    .putExtra(Constants.KEY_RESULT_RECEIVER, resultReceiver(it))
            )
        }.compose(handleAuthCodeError())

    /**
     * Custom Tabs (기본 브라우저) 를 사용하여 authorization code 를 발급 받는다.
     *
     * @param context instance for starting an activity
     * @param clientId native app key
     *
     * @return [Single] that will emit authorization code
     */
    @JvmOverloads
    fun authorize(
        context: Context,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        autoLogin: Boolean? = null,
        clientId: String = KakaoSdk.applicationContextInfo.appKey,
        redirectUri: String = "kakao${KakaoSdk.applicationContextInfo.appKey}://oauth",
        kaHeader: String = KakaoSdk.applicationContextInfo.kaHeader
    ): Single<String> =
        Single.create<String> {
            val fullUri = UriUtility.authorizeUri(
                clientId = clientId,
                redirectUri = redirectUri,
                kaHeader = kaHeader,
                channelPublicIds = channelPublicIds,
                serviceTerms = serviceTerms,
                autoLogin = autoLogin
            )
            context.startActivity(authCodeIntent(context, fullUri, redirectUri, resultReceiver(it)))
        }.compose(handleAuthCodeError())

    /**
     * 유저의 동의항목을 업데이트할 수 있는 authorization code 를 발급받는다.
     *
     * @param context [Context] instance for starting an activity
     * @param scopes list of scopes to be updated
     *
     * @return [Single] that will emit authorization code
     */
    @JvmOverloads
    fun authorizeWithNewScopes(
        context: Context,
        scopes: List<String>,
        channelPublicIds: List<String>? = null,
        serviceTerms: List<String>? = null,
        autoLogin: Boolean? = null,
        redirectUri: String = "kakao${KakaoSdk.applicationContextInfo.appKey}://oauth",
        clientId: String = KakaoSdk.applicationContextInfo.appKey
    ): Single<String> =
        authApiClient.agt(clientId).flatMap {
            Single.create<String> { emitter ->
                val uri = UriUtility.authorizeUri(
                    clientId = clientId,
                    agt = it.agt,
                    redirectUri = redirectUri,
                    scopes = scopes,
                    channelPublicIds = channelPublicIds,
                    serviceTerms = serviceTerms,
                    autoLogin = autoLogin
                )
                val intent = authCodeIntent(context, uri, redirectUri, resultReceiver(emitter))
                context.startActivity(intent)
            }
        }.compose(handleAuthCodeError())

    fun isTalkLoginAvailable(context: Context): Boolean =
        intentResolveClient.resolveTalkIntent(context, baseTalkLoginIntent()) != null

    private fun baseTalkLoginIntent(): Intent =
        Intent().setAction("com.kakao.talk.intent.action.CAPRI_LOGGED_IN_ACTIVITY")
            .addCategory(Intent.CATEGORY_DEFAULT)

    @JvmSynthetic
    internal fun talkLoginIntent(
        clientId: String,
        redirectUri: String,
        kaHeader: String,
        extras: Bundle
    ): Intent =
        baseTalkLoginIntent()
            .putExtra(Constants.EXTRA_APPLICATION_KEY, clientId)
            .putExtra(Constants.EXTRA_REDIRECT_URI, redirectUri)
            .putExtra(Constants.EXTRA_KA_HEADER, kaHeader)
            .putExtra(Constants.EXTRA_EXTRAPARAMS, extras)

    @JvmSynthetic
    internal fun authCodeIntent(
        context: Context,
        fullUri: Uri,
        redirectUri: String,
        resultReceiver: ResultReceiver
    ) =
        Intent(context, AuthCodeCustomTabsActivity::class.java)
            .putExtra(Constants.KEY_BUNDLE, Bundle().apply {
                putParcelable(Constants.KEY_RESULT_RECEIVER, resultReceiver)
                putParcelable(Constants.KEY_FULL_URI, fullUri)
                putString(Constants.KEY_REDIRECT_URI, redirectUri)
            })

    @JvmSynthetic
    internal fun resultReceiver(emitter: SingleEmitter<String>): ResultReceiver =
        object : ResultReceiver(Handler(Looper.getMainLooper())) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                this@RxAuthCodeClient.onReceivedResult(resultCode, resultData, emitter)
            }
        }

    @JvmSynthetic
    internal fun onReceivedResult(
        resultCode: Int,
        resultData: Bundle?,
        emitter: SingleEmitter<String>
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = resultData?.getParcelable<Uri>(Constants.KEY_URL)
                val code = uri?.getQueryParameter(Constants.CODE)
                if (code != null) {
                    emitter.onSuccess(code)
                    return
                }
                val error = uri?.getQueryParameter(Constants.ERROR) as String
                val errorDescription = uri.getQueryParameter(Constants.ERROR_DESCRIPTION) as String
                emitter.onError(
                    OAuthError(
                        HttpURLConnection.HTTP_MOVED_TEMP,
                        KakaoJson.fromJson(
                            error,
                            AuthErrorCause::class.java
                        )
                            ?: AuthErrorCause.Unknown,
                        AuthErrorResponse(error, errorDescription)
                    )
                )
            }
            Activity.RESULT_CANCELED -> {
                val exception =
                    resultData?.getSerializable(Constants.KEY_EXCEPTION)
                            as KakaoSdkError
                emitter.onError(exception)
            }
            else -> throw IllegalArgumentException("Unknown resultCode in RxAuthCodeClient#onReceivedResult()")
        }
    }

    internal fun <T> handleAuthCodeError(): SingleTransformer<T, T> = SingleTransformer {
        it.doOnError { SdkLogger.rx.e(it) }
            .doOnSuccess { SdkLogger.rx.i(it.toString()) }
    }
}

val AuthCodeClient.Companion.rx by lazy { RxAuthCodeClient() }