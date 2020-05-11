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
@file:JvmName("StoryApiClientKt")

package com.kakao.sdk.story

import com.kakao.sdk.auth.network.TokenBasedApiInterceptor
import com.kakao.sdk.auth.network.kapiWithOAuth
import com.kakao.sdk.common.ApiFactory
import com.kakao.sdk.common.KakaoJson
import com.kakao.sdk.network.RxApiInterceptor
import com.kakao.sdk.story.model.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 카카오 Open API 의 카카오스토리 API 호출을 담당하는 클래스입니다.
 *
 * @author kevin.kang. Created on 2018. 3. 20..
 */
class RxStoryApiClient(
    private val api: RxStoryApi = ApiFactory.kapiWithOAuth.create(RxStoryApi::class.java),
    private val apiInterceptor: TokenBasedApiInterceptor = TokenBasedApiInterceptor.instance
) {
    /**
     * 사용자가 카카오스토리 사용자인지 아닌지를 판별합니다.
     */
    fun isStoryUser(): Single<Boolean> {
        return api.isStoryUser()
            .map { it.isStoryUser }
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 로그인된 사용자의 카카오스토리 프로필 정보를 얻을 수 있습니다.
     */
    @JvmOverloads
    fun profile(secureResource: Boolean? = true): Single<StoryProfile> {
        return api.profile(secureResource)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 카카오스토리의 특정 내스토리 정보를 얻을 수 있습니다. comments, likes등의 상세정보도 포함됩니다.
     */
    fun story(id: String): Single<Story> {
        return api.story(id)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 카카오스토리의 복수개의 내스토리 정보들을 얻을 수 있습니다.
     * 단, comments, likes 등의 상세정보는 없으며 이는 내스토리 정보 요청 story(id:)을 통해 얻을 수 있습니다.
     */
    @JvmOverloads
    fun stories(lastId: String? = null): Single<List<Story>> {
        return api.stories(lastId)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 카카오스토리에 글(노트)을 포스팅합니다.
     */
    @JvmOverloads
    fun postNote(
        content: String,
        permission: Story.Permission = Story.Permission.PUBLIC,
        enableShare: Boolean = true,
        androidExecParams: Map<String, String>? = null,
        iosExecParams: Map<String, String>? = null,
        androidMarketParams: Map<String, String>? = null,
        iosMarketParams: Map<String, String>? = null
    ): Single<String> {
        return api.postNote(
            content, permission, enableShare, androidExecParams, iosExecParams, androidMarketParams,
            iosMarketParams
        ).map { it.id }
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 카카오스토리에 링크(스크랩 정보)를 포스팅합니다.
     * 먼저 포스팅하고자 하는 URL로 스크랩 API를 호출한 후 반환된 링크 정보를 파라미터로 전달하여 포스팅 해야 합니다.
     *
     * @see linkInfo
     */
    @JvmOverloads
    fun postLink(
        linkInfo: LinkInfo,
        content: String,
        permission: Story.Permission = Story.Permission.PUBLIC,
        enableShare: Boolean = true,
        androidExecParams: Map<String, String>? = null,
        iosExecParams: Map<String, String>? = null,
        androidMarketParams: Map<String, String>? = null,
        iosMarketParams: Map<String, String>? = null
    ): Single<String> {

        return api.postLink(
            linkInfo, content, permission, enableShare, androidExecParams, iosExecParams,
            androidMarketParams, iosMarketParams
        ).map { it.id }
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 카카오스토리에 사진(들)을 포스팅합니다.
     */
    @JvmOverloads
    fun postPhoto(
        images: List<String>,
        content: String,
        permission: Story.Permission = Story.Permission.PUBLIC,
        enableShare: Boolean = true,
        androidExecParams: Map<String, String>? = null,
        iosExecParams: Map<String, String>? = null,
        androidMarketParams: Map<String, String>? = null,
        iosMarketParams: Map<String, String>? = null
    ): Single<String> {
        return api.postPhoto(
            KakaoJson.toJson(images), content, permission, enableShare,
            androidExecParams, iosExecParams, androidMarketParams, iosMarketParams
        ).map { it.id }
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 카카오스토리의 특정 내스토리 정보를 지울 수 있습니다.
     */
    fun deleteStory(id: String): Completable {
        return api.deleteStory(id)
            .compose(RxApiInterceptor.handleCompletableError())
            .compose(apiInterceptor.handleCompletableError())
    }

    /**
     * 포스팅하고자 하는 URL 을 스크랩하여 링크 정보를 생성합니다.
     */
    fun linkInfo(url: String): Single<LinkInfo> {
        return api.linkInfo(url)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 로컬 이미지 파일 여러장을 카카오스토리에 업로드합니다.
     */
    fun uploadImage(images: List<File>): Single<List<String>> {
        return Single.just(
            images.map { Pair(it.name, RequestBody.create(MediaType.parse("image/*"), it)) }
                .mapIndexed { index, pair ->
                    MultipartBody.Part.createFormData(
                        "${Constants.FILE}_$index",
                        pair.first,
                        pair.second
                    )
                })
            .flatMap { api.upload(it) }
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    companion object {
        @JvmStatic
        val instance by lazy { StoryApiClient.rx }
    }
}

val StoryApiClient.Companion.rx by lazy { RxStoryApiClient() }