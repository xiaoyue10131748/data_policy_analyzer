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

import android.net.Uri
import com.kakao.sdk.auth.Constants

/**
 * @author kevin.kang. Created on 2019-10-30..
 */
object TestUriUtility {
    fun successfulRedirectUri(): Uri {
        return Uri.Builder().scheme("kakao123456").authority("oauth")
            .appendQueryParameter(Constants.CODE, "authorization_code").build()
    }

    fun failedRedirectUri(): Uri {
        return Uri.Builder().scheme("kakao123456").authority("oauth")
            .appendQueryParameter(Constants.ERROR, "invalid_grant")
            .appendQueryParameter(Constants.ERROR_DESCRIPTION, "error_description")
            .build()
    }
}