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
package com.kakao.sdk.user

import com.kakao.sdk.common.json.IntDate
import com.kakao.sdk.user.Constants
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.User
import com.kakao.sdk.user.model.UserServiceTerms
import com.kakao.sdk.user.model.UserShippingAddresses
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import java.util.*

/**
 * @suppress
 * @author kevin.kang. Created on 2018. 3. 24..
 */
interface RxUserApi {
    @GET(Constants.V2_ME_PATH)
    fun me(
        @Query(Constants.SECURE_RESOURCE) secureResource: Boolean = true,
        @Query(Constants.PROPERTYKEYS) properties: String? = null
    ): Single<User>

    @GET(Constants.V1_ACCESS_TOKEN_INFO_PATH)
    fun accessTokenInfo(): Single<AccessTokenInfo>

    @POST(Constants.V1_UPDATE_PROFILE_PATH)
    @FormUrlEncoded
    fun updateProfile(
        @Field(Constants.PROPERTIES) properties: Map<String, String>
    ): Completable

    @POST(Constants.V1_LOGOUT_PATH)
    fun logout(): Completable

    @POST(Constants.V1_UNLINK_PATH)
    fun unlink(): Completable

    @GET(Constants.V1_SHIPPING_ADDRESSES_PATH)
    fun shippingAddresses(
        @Query(Constants.ADDRESS_ID) addressId: Long? = null,
        @IntDate @Query(Constants.FROM_UPDATED_AT) fromUpdateAt: Date? = null,
        @Query(Constants.PAGE_SIZE) pageSize: Int? = null
    ): Single<UserShippingAddresses>

    /**
     * User 가 3rd의 동의항목에 동의한 내역을 반환한다.
     */
    @GET(Constants.V1_SERVICE_TERMS_PATH)
    fun serviceTerms(): Single<UserServiceTerms>
}