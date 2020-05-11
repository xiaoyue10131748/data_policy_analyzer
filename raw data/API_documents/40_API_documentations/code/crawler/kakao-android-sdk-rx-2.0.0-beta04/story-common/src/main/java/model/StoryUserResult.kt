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
package com.kakao.sdk.story.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.story.Constants
import kotlinx.android.parcel.Parcelize

/**
 * @suppress
 * @author kevin.kang. Created on 2018. 3. 21..
 */
@Parcelize
data class StoryUserResult(@SerializedName(Constants.IS_STORY_USER) val isStoryUser: Boolean) :
    Parcelable