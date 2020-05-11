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
@file:JvmName("UserApiClientKt")

package com.kakao.sdk.user

import com.kakao.sdk.auth.AccessTokenRepo
import com.kakao.sdk.auth.network.TokenBasedApiInterceptor
import com.kakao.sdk.auth.network.kapiWithOAuth
import com.kakao.sdk.common.ApiFactory
import com.kakao.sdk.network.RxApiInterceptor
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.User
import com.kakao.sdk.user.model.UserServiceTerms
import com.kakao.sdk.user.model.UserShippingAddresses
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

/**
 * 카카오 Open API의 사용자관리 API 호출을 담당하는 클래스입니다.
 *
 * @since 2.0.0
 * @author kevin.kang. Created on 2018. 4. 2..
 */
class RxUserApiClient(
    private val userApi: RxUserApi = ApiFactory.kapiWithOAuth.create(RxUserApi::class.java),
    val apiInterceptor: TokenBasedApiInterceptor = TokenBasedApiInterceptor.instance,
    private val accessTokenRepo: AccessTokenRepo = AccessTokenRepo.instance
) {

    /**
     * 사용자에 대한 다양한 정보를 얻을 수 있습니다.
     *
     * @since 2.0.0
     */
    @JvmOverloads
    fun me(secureReSource: Boolean = true): Single<User> =
        userApi.me(secureReSource)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())

    /**
     * User 클래스에서 제공되고 있는 사용자의 부가정보를 신규저장 및 수정할 수 있습니다.
     *
     * 저장 가능한 키 이름은 개발자사이트 앱 설정의 사용자 관리 > 사용자 목록 및 프로퍼티 메뉴에서 확인하실 수 있습니다.
     * 앱 연결 시 기본 저장되는 nickname, profile_image, thumbnail_image 값도 덮어쓰기 가능하며 새로운 컬럼을 추가하면 해당 키 이름으로 값을 저장할 수 있습니다.
     *
     * @since 2.0.0
     */
    fun updateProfile(properties: Map<String, String>): Completable =
        userApi.updateProfile(properties)
            .compose(RxApiInterceptor.handleCompletableError())
            .compose(apiInterceptor.handleCompletableError())

    /**
     * 현재 토큰의 기본적인 정보를 조회합니다.
     *
     * [me] 에서 제공되는 다양한 사용자 정보 없이 가볍게 토큰의 유효성을 체크하는 용도로 사용하는 경우 추천합니다.
     * 액세스토큰이 만려되어있는 경우 리프레시토큰으로 갱신된 새로운 액세스토큰의 정보를 반환합니다.
     *
     * @since 2.0.0
     */
    fun accessTokenInfo(): Single<AccessTokenInfo> =
        userApi.accessTokenInfo()
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())

    /**
     * 토큰을 강제로 만료시킵니다. 같은 사용자가 여러개의 토큰을 발급 받은 경우 로그아웃 요청에 사용된 토큰만 만료됩니다.
     *
     * 클라이언트에 저장되어 있는 액세스토큰과 리프레시토큰을 삭제합니다.
     *
     * @since 2.0.0
     */
    fun logout(): Completable =
        userApi.logout()
            .compose(RxApiInterceptor.handleCompletableError())
            .compose(apiInterceptor.handleCompletableError())
            .doOnEvent { accessTokenRepo.clear() }

    /**
     * 카카오 플랫폼 서비스와 앱 연결을 해제합니다.
     *
     * 클라이언트에 저장되어 있는 액세스토큰과 리프레시토큰을 삭제합니다.
     *
     * @since 2.0.0
     */
    fun unlink(): Completable =
        userApi.unlink()
            .compose(RxApiInterceptor.handleCompletableError())
            .compose(apiInterceptor.handleCompletableError())
            .doOnEvent { accessTokenRepo.clear() }

    /**
     * 앱에 가입한 사용자의 배송지 정보를 얻어갑니다.
     *
     * @param fromUpdateAt
     * @param pageSize
     *
     * @since 2.0.0
     */
    @JvmOverloads
    fun shippingAddresses(
        fromUpdateAt: Date? = null,
        pageSize: Int? = null
    ): Single<UserShippingAddresses> {
        return userApi.shippingAddresses(fromUpdateAt = fromUpdateAt, pageSize = pageSize)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 앱에 가입한 사용자의 배송지 정보를 얻어갑니다.
     *
     * @param addressId 가져올 배송지 id
     *
     * @since 2.0.0
     */
    fun shippingAddresses(addressId: Long): Single<UserShippingAddresses> {
        return userApi.shippingAddresses(addressId)
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    /**
     * 사용자가 카카오 간편가입을 통해 동의한 서비스 약관 내역을 반환합니다.
     *
     * @since 2.0.0
     */
    fun serviceTerms(): Single<UserServiceTerms> {
        return userApi.serviceTerms()
            .compose(RxApiInterceptor.handleApiError())
            .compose(apiInterceptor.handleApiError())
    }

    companion object {
        /**
         * User API 를 호출하기 위한 rx singleton
         */
        @JvmStatic
        val instance by lazy { UserApiClient.rx }
    }
}

/**
 * User API 를 호출하기 위한 rx singleton
 */
val UserApiClient.Companion.rx by lazy { RxUserApiClient() }