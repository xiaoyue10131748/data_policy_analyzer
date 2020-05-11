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
package com.kakao.sdk.java.sample;

import android.app.Application;
import android.util.Log;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.SdkLogger;
import com.kakao.sdk.link.LinkClient;
import com.kakao.sdk.link.LinkClientKt;
import com.kakao.sdk.link.RxLinkClient;
import com.kakao.sdk.navi.NaviClient;
import com.kakao.sdk.network.RxSdkLogger;
import com.kakao.sdk.network.SdkLoggerKt;
import com.kakao.sdk.story.RxStoryApiClient;
import com.kakao.sdk.story.StoryApiClient;
import com.kakao.sdk.story.StoryApiClientKt;
import com.kakao.sdk.talk.RxTalkApiClient;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.TalkApiClientKt;
import com.kakao.sdk.user.RxUserApiClient;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.UserApiClientKt;

/**
 * @author kevin.kang. Created on 2019-10-29..
 */
public class JavaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "9f9de684c354a72d2eb2a540a11441c2");
        RxSdkLogger.getInstance().getObservable().doOnNext(o -> Log.d("KakaoSdk", o.toString())).subscribe();

        SdkLoggerKt.getRx(SdkLogger.Companion);
        RxSdkLogger.getInstance();

        UserApiClientKt.getRx(UserApiClient.Companion);
        RxUserApiClient.getInstance();

        TalkApiClientKt.getRx(TalkApiClient.Companion);
        RxTalkApiClient.getInstance();

        StoryApiClientKt.getRx(StoryApiClient.Companion);
        RxStoryApiClient.getInstance();

        LinkClientKt.getRx(LinkClient.Companion);
        RxLinkClient.getInstance();

        NaviClient.getInstance();

    }
}
