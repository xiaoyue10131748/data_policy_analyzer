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
package com.kakao.sdk.auth.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.auth.Constants
import kotlinx.android.parcel.Parcelize

/**
 * @author kevin.kang. Created on 2018. 3. 24..
 */
@Parcelize
data class AccessTokenResponse(
    val accessToken: String,
    val refreshToken: String? = null,
    @SerializedName(Constants.EXPIRES_IN) val accessTokenExpiresIn: Long,
    val refreshTokenExpiresIn: Long? = null,
    val tokenType: String,
    @SerializedName(Constants.SCOPE) val scopes: String? = null
) : Parcelable