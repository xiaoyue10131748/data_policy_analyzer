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
@file:JvmName("LinkClientKt")

package com.kakao.sdk.link

import android.content.Context
import android.content.Intent
import com.kakao.sdk.common.ApiFactory
import com.kakao.sdk.link.model.ImageUploadResult
import com.kakao.sdk.template.DefaultTemplate
import com.kakao.sdk.network.RxApiInterceptor
import com.kakao.sdk.network.kapi
import io.reactivex.Single
import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * 카카오링크 호출을 담당하는 클래스
 *
 * @author kevin.kang. Created on 19/02/2019..
 */
class RxLinkClient(
    private val api: RxLinkApi = ApiFactory.kapi.create(RxLinkApi::class.java),
    private val linkIntentClient: KakaoLinkIntentClient = KakaoLinkIntentClient.instance
) {

    fun isKakaoLinkAvailable(context: Context): Boolean {
        return linkIntentClient.isKakaoLinkAvailable(context)
    }

    /**
     * 개발자사이트에서 생성한 메시지 템플릿을 카카오톡으로 공유합니다. 템플릿을 생성하는 방법은 https://developers.kakao.com/docs/template 을 참고하시기 바랍니다.
     */
    fun customTemplate(
        context: Context,
        templateId: Long,
        templateArgs: Map<String, String>? = null,
        serverCallbackArgs: Map<String, String>? = null
    ): Single<Intent> {
        return api.validateCustom(templateId, templateArgs)
            .compose(RxApiInterceptor.handleApiError())
            .map { linkIntentClient.linkIntentFromResponse(context, it, serverCallbackArgs) }
    }

    /**
     * 기본 템플릿을 카카오톡으로 공유합니다.
     */
    fun defaultTemplate(
        context: Context,
        defaultTemplate: DefaultTemplate,
        serverCallbackArgs: Map<String, String>? = null
    ): Single<Intent> {
        return api.validateDefault(defaultTemplate)
            .compose(RxApiInterceptor.handleApiError())
            .map { linkIntentClient.linkIntentFromResponse(context, it, serverCallbackArgs) }
    }

    /**
     * 지정된 URL 을 스크랩하여 만들어진 템플릿을 카카오톡으로 공유합니다.
     */
    fun scrapTemplate(
        context: Context,
        url: String,
        templateId: Long? = null,
        templateArgs: Map<String, String>? = null,
        serverCallbackArgs: Map<String, String>? = null
    ): Single<Intent> {
        return api.validateScrap(url, templateId, templateArgs)
            .compose(RxApiInterceptor.handleApiError())
            .map { linkIntentClient.linkIntentFromResponse(context, it, serverCallbackArgs) }
    }

    /**
     * 카카오링크 컨텐츠 이미지로 활용하기 위해 로컬 이미지를 카카오 이미지 서버로 업로드 합니다.
     */
    fun uploadImage(image: File, secureResource: Boolean = true): Single<ImageUploadResult> =
        Single.just(image).map { Pair(it.name, RequestBody.create(MediaType.parse("image/*"), it)) }
            .map { MultipartBody.Part.createFormData("file", it.first, it.second) }
            .flatMap { api.uploadImage(image = it, secureResource = secureResource) }
            .compose(RxApiInterceptor.handleApiError())

    /**
     * 카카오링크 컨텐츠 이미지로 활용하기 위해 원격 이미지를 카카오 이미지 서버로 스크랩 합니다.
     */
    fun scrapImage(imageUrl: String, secureResource: Boolean = true): Single<ImageUploadResult> =
        api.scrapImage(imageUrl, secureResource)
            .compose(RxApiInterceptor.handleApiError())

    companion object {
        @JvmStatic
        val instance by lazy { RxLinkClient() }
    }
}

val LinkClient.Companion.rx by lazy { RxLinkClient() }