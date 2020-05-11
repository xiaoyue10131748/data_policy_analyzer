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
package com.kakao.sdk.talk.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.common.KakaoJson
import kotlinx.android.parcel.Parcelize

/**
 * 친구 목록 조회 API 응답 클래스
 *
 * @author kevin.kang. Created on 2018. 3. 22..
 */
@Parcelize
data class Friends<T : Parcelable>(val totalCount: Int, val elements: List<T>) : Parcelable {
    companion object {
        fun <T : Parcelable> fromJson(string: String, clazz: Class<T>): Friends<T> =
            KakaoJson.parameterizedFromJson(string, Friends::class.java, clazz)
    }
}

/**
 * 카카오톡 친구
 * @property id 사용자 아이디
 * @property uuid 메시지를 전송하기 위한 고유 아이디. 사용자의 계정 상태에 따라 이 정보는 바뀔 수 있습니다. 앱내의 사용자 식별자로 저장 사용되는 것은 권장하지 않습니다.
 * @property profileNickname 친구의 닉네임
 * @property profileThumbnailImage 썸네일 이미지 URL
 * @property favorite 즐겨찾기 추가 여부
 */
@Parcelize
data class Friend(
    val id: Long,
    val uuid: String,
    val profileNickname: String,
    val profileThumbnailImage: String,
    val favorite: Boolean
) : Parcelable

enum class Order {
    @SerializedName("asc")
    ASC,
    @SerializedName("desc")
    DESC
}