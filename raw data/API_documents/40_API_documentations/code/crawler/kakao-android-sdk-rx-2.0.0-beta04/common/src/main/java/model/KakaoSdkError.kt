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
package com.kakao.sdk.common.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.json.UnknownValue
import kotlinx.android.parcel.Parcelize
import java.lang.RuntimeException

/**
 * 카카오 SDK 를 사용하면서 발생하는 모든 에러를 나타냅니다.
 *
 * @author kevin.kang. Created on 2019-06-04..
 */
@Suppress("Unused")
sealed class KakaoSdkError(open val msg: String) : RuntimeException(msg)

/**
 * API 호출 에러
 */
@Parcelize
data class ApiError(
    val statusCode: Int,
    val reason: ApiErrorCause,
    val response: ApiErrorResponse
) : KakaoSdkError(response.msg), Parcelable {
    companion object {
        fun fromScopes(scopes: List<String>): ApiError {
            return ApiError(
                403,
                ApiErrorCause.InvalidScope,
                ApiErrorResponse(
                    ApiErrorCause.InvalidScope.errorCode,
                    msg = "",
                    requiredScopes = scopes
                )
            )
        }
    }
}

/**
 * 로그인 에러
 */
@Parcelize
data class OAuthError(
    val statusCode: Int,
    val reason: AuthErrorCause,
    val response: AuthErrorResponse
) : KakaoSdkError(response.errorDescription), Parcelable

/**
 * SDK 내에서 발생하는 클라이언트 에러
 */
@Parcelize
data class ClientError(
    val reason: ClientErrorCause,
    override val msg: String = "Client-side error"
) :
    KakaoSdkError(msg), Parcelable

enum class AuthErrorCause {
    @SerializedName("invalid_request")
    InvalidRequest,
    @SerializedName("invalid_scope")
    InvalidScope,
    @SerializedName("invalid_grant")
    InvalidGrant,
    @SerializedName("misconfigured")
    Misconfigured,
    @SerializedName("unauthorized")
    Unauthorized,
    @SerializedName("access_denied")
    AccessDenied,
    @SerializedName("server_error")
    ServerError,
    @SerializedName("auto_login")
    AutoLogin,
    @UnknownValue
    Unknown,
}

enum class ApiErrorCause(val errorCode: Int) {
    @SerializedName("-1")
    InternalError(-1),
    @SerializedName("-2")
    IllegalParams(-2),
    @SerializedName("-3")
    UnsupportedApi(-3),
    @SerializedName("-4")
    BlockedAction(-4),
    @SerializedName("-5")
    PermissionDenied(-5),
    @SerializedName("-9")
    DeprecatedApi(-9),
    @SerializedName("-10")
    ApiLimitExceeded(-10),
    @SerializedName("-301")
    AppDoesNotExist(-301),
    @SerializedName("-401")
    InvalidToken(-401),
    @SerializedName("-403")
    InvalidOrigin(-403),
    @SerializedName("-603")
    TimeOut(-603),
    @SerializedName("-903")
    DeveloperDoesNotExist(-903),

    // authorized API
    @SerializedName("-101")
    NotRegisteredUser(-101),
    @SerializedName("-102")
    AlreadyRegisteredUser(-102),
    @SerializedName("-103")
    AccountDoesNotExist(-103),
    @SerializedName("-402")
    InvalidScope(-402),
    @SerializedName("-405")
    AgeVerificationRequired(-405),
    @SerializedName("-406")
    UnderAgeLimit(-406),

    // user API
    @SerializedName("-201")
    PropertyKeyDoesNotExist(-201),

    // talk API
    @SerializedName("-501")
    NotTalkUser(-501),
    @SerializedName("-502")
    NotFriend(-502),
    @SerializedName("-504")
    UserDeviceUnsupported(-504),
    @SerializedName("-530")
    TalkMessageDisabled(-530),
    @SerializedName("-531")
    TalkSendMessageMonthlyLimitExceed(-531),
    @SerializedName("-532")
    TalkSendMessageDailyLimitExceed(-532),

    @SerializedName("-601")
    NotStoryUser(-601),
    @SerializedName("-602")
    StoryImageUploadSizeExceeded(-602),
    @SerializedName("-604")
    StoryInvalidScrapUrl(-604),
    @SerializedName("-605")
    StoryInvalidPostId(-605),
    @SerializedName("-606")
    StoryMaxUploadCountExceed(-606),

    @UnknownValue
    Unknown(Int.MAX_VALUE);
}

enum class ClientErrorCause {
    Unknown,
    Cancelled,
    TokenNotFound,
    NetworkError,
    NotSupported,
    BadParameter,
    IllegalState
}


