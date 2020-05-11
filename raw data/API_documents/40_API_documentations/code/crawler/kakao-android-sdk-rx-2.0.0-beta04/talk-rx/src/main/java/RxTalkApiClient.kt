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
@file:JvmName("TalkApiClientKt")

package com.kakao.sdk.talk

import android.net.Uri
import com.kakao.sdk.auth.network.TokenBasedApiInterceptor
import com.kakao.sdk.auth.network.kapiWithOAuth
import com.kakao.sdk.common.ApiFactory
import com.kakao.sdk.common.model.ApplicationInfo
import com.kakao.sdk.common.model.ContextInfo
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.KakaoJson
import com.kakao.sdk.template.DefaultTemplate
import com.kakao.sdk.network.RxApiInterceptor
import com.kakao.sdk.talk.model.*
import io.reactivex.Completable
import io.reactivex.Single

/**
 * 카카오 Open API 의 카카오톡 API 호출을 담당하는 클래스입니다.
 *
 * @author kevin.kang. Created on 2018. 3. 30..
 */
class RxTalkApiClient(
    private val api: RxTalkApi = ApiFactory.kapiWithOAuth.create(RxTalkApi::class.java),
    val apiInterceptor: TokenBasedApiInterceptor = TokenBasedApiInterceptor.instance,
    private val applicationInfo: ApplicationInfo = KakaoSdk.applicationContextInfo,
    private val contextInfo: ContextInfo = KakaoSdk.applicationContextInfo
) {

    /**
     * 로그인된 사용자의 카카오톡 프로필 정보를 얻을 수 있습니다.
     */
    @JvmOverloads
    fun profile(
        secureResource: Boolean? = true
    ): Single<TalkProfile> =
        api.profile(secureResource)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())

    /**
     * 카카오톡 친구 목록을 조회합니다.
     */
    @JvmOverloads
    fun friends(
        offset: Int? = null,
        limit: Int? = null,
        order: Order? = null,
        secureResource: Boolean? = true
    ): Single<Friends<Friend>> = api.friends(secureResource, offset, limit, order)
        .compose(RxApiInterceptor.handleApiError())
        .compose(apiInterceptor.handleApiError())

    /**
     * 개발자사이트에서 생성한 서비스만의 커스텀 메시지 템플릿을 사용하여, 카카오톡의 나와의 채팅방으로 메시지를 전송합니다.
     * 템플릿을 생성하는 방법은 https://developers.kakao.com/docs/template 을 참고하시기 바랍니다.
     */
    @JvmOverloads
    fun sendCustomMemo(templateId: Long, templateArgs: Map<String, String>? = null): Completable =
        api.sendCustomMemo(
            templateId,
            templateArgs
        ).compose(RxApiInterceptor.handleCompletableError())
            .compose(apiInterceptor.handleCompletableError())

    /**
     * 기본 템플릿을 이용하여, 카카오톡의 나와의 채팅방으로 메시지를 전송합니다.
     */
    fun sendDefaultMemo(templateParams: DefaultTemplate): Completable =
        api.sendDefaultMemo(templateParams)
            .compose(RxApiInterceptor.handleCompletableError())
            .compose(apiInterceptor.handleCompletableError())

    /**
     * 지정된 URL 을 스크랩하여, 카카오톡의 나와의 채팅방으로 메시지를 전송합니다.
     */
    @JvmOverloads
    fun sendScrapMemo(
        requestUrl: String,
        templateId: Long? = null,
        templateArgs: Map<String, String>? = null
    ): Completable =
        api.sendScrapMemo(requestUrl, templateId, templateArgs)
            .compose(RxApiInterceptor.handleCompletableError())
            .compose(apiInterceptor.handleCompletableError())

    /**
     * 개발자사이트에서 생성한 메시지 템플릿을 사용하여, 조회한 친구를 대상으로 카카오톡으로 메시지를 전송합니다.
     * 템플릿을 생성하는 방법은 https://developers.kakao.com/docs/template 을 참고하시기 바랍니다.
     */
    @JvmOverloads
    fun sendCustomMessage(
        receiverUuids: List<String>,
        templateId: Long,
        templateArgs: Map<String, String>? = null
    ): Single<MessageSendResult> = api.sendCustomMessage(
        KakaoJson.toJson(receiverUuids),
        templateId,
        templateArgs
    ).compose(RxApiInterceptor.handleApiError())
        .compose(apiInterceptor.handleApiError())

    /**
     * 기본 템플릿을 사용하여, 조회한 친구를 대상으로 카카오톡으로 메시지를 전송합니다.
     */
    fun sendDefaultMessage(
        receiverUuids: List<String>,
        templateParams: DefaultTemplate
    ): Single<MessageSendResult> =
        api.sendDefaultMessage(KakaoJson.toJson(receiverUuids), templateParams)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())

    /**
     * 지정된 URL을 스크랩하여, 조회한 친구를 대상으로 카카오톡으로 메시지를 전송합니다.
     * 스크랩 커스텀 템플릿 가이드를 참고하여 템플릿을 직접 만들고 스크랩 메시지 전송에 이용할 수도 있습니다.
     */
    @JvmOverloads
    fun sendScrapMessage(
        receiverUuids: List<String>,
        requestUrl: String,
        templateId: Long? = null,
        templateArgs: Map<String, String>? = null
    ): Single<MessageSendResult> =
        api.sendScrapMessage(KakaoJson.toJson(receiverUuids), requestUrl, templateId, templateArgs)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())

    /**
     * 사용자가 특정 카카오톡 채널을 추가했는지 확인합니다.
     */
    @JvmOverloads
    fun channels(publicIds: List<String>? = null): Single<ChannelRelations> =
        api.channels(
            if (publicIds == null) null else KakaoJson.toJson(
                publicIds
            )
        ).compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())

    /**
     * 카카오톡 채널을 추가하기 위한 URL 을 반환합니다. URL 을 브라우저나 웹뷰에서 로드하면 브릿지 웹페이지를 통해 카카오톡을 실행합니다.
     *
     * channelPublicId: 카카오톡 채널 홈 URL 에 들어간 {_영문}으로 구성된 고유 아이디입니다.
     * 홈 URL 은 카카오톡 채널 관리자센터 > 관리 > 상세설정 페이지에서 확인할 수 있습니다.
     *
     * @see com.kakao.sdk.common.util.KakaoCustomTabsClient
     */
    fun addChannelUrl(channelPublicId: String): Uri {
        return baseUri(appKey = applicationInfo.appKey, kaHeader = contextInfo.kaHeader)
            .path("$channelPublicId/${Constants.FRIEND}").build()
    }

    /**
     * 카카오톡 채널 1:1 대화방 실행을 위한 URL 을 반환합니다. URL 을 브라우저나 웹뷰에서 로드하면 브릿지 웹페이지를 통해 카카오톡을 실행합니다.
     *
     * channelPublicId: 카카오톡 채널 홈 URL 에 들어간 {_영문}으로 구성된 고유 아이디입니다.
     * 홈 URL 은 카카오톡 채널 관리자센터 > 관리 > 상세설정 페이지에서 확인할 수 있습니다.
     *
     * @see com.kakao.sdk.common.util.KakaoCustomTabsClient
     */
    fun channelChatUrl(channelPublicId: String): Uri {
        return baseUri(appKey = applicationInfo.appKey, kaHeader = contextInfo.kaHeader)
            .path("$channelPublicId/${Constants.CHAT}").build()
    }

    private fun baseUri(appKey: String, kaHeader: String): Uri.Builder {
        return Uri.Builder().scheme(com.kakao.sdk.common.Constants.SCHEME)
            .authority(KakaoSdk.serverHosts.channel)
            .appendQueryParameter(com.kakao.sdk.common.Constants.APP_KEY, appKey)
            .appendQueryParameter(Constants.KAKAO_AGENT, kaHeader)
            .appendQueryParameter(Constants.API_VER, Constants.API_VER_10)
    }

    companion object {
        @JvmStatic
        val instance by lazy { TalkApiClient.rx }
    }
}

/**
 * 카카오톡
 */
val TalkApiClient.Companion.rx by lazy { RxTalkApiClient() }
