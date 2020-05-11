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
package com.kakao.sdk.sample.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar.*
import com.google.android.material.snackbar.Snackbar
import com.kakao.sdk.auth.*
import com.kakao.sdk.auth.network.TokenBasedApiInterceptor
import com.kakao.sdk.common.model.ApiError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.WebSharerClient
import com.kakao.sdk.link.rx
import com.kakao.sdk.navi.NaviClient
import com.kakao.sdk.navi.model.CoordType
import com.kakao.sdk.navi.model.Location
import com.kakao.sdk.navi.model.NaviOptions
import com.kakao.sdk.story.StoryApiClient
import com.kakao.sdk.story.rx
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.talk.rx
import com.kakao.sdk.template.*
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import com.kakao.sdk.user.rx
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_open.*
import java.io.File
import java.io.FileOutputStream

class OpenFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var disposables: CompositeDisposable

    val onSuccess = Consumer<Any> {
        showSnackbar(it)
    }

    val onError = Consumer<Throwable> {
        showSnackbar(it)
    }

    val onCompletableSuccess = Action {
        showSnackbar("API succeeded.")
    }

    private fun showSnackbar(any: Any) {
        val snackbar = Snackbar.make(view!!, any.toString(), LENGTH_LONG)
        val layout = snackbar.view as Snackbar.SnackbarLayout
        layout.minimumHeight = 100
        snackbar.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposables = CompositeDisposable()

        viewManager = LinearLayoutManager(context!!)
        viewAdapter = ApiAdapter(
            listOf(
                ApiAdapter.Item.Header("Kakao Login"),
                ApiAdapter.Item.ApiItem("isTalkLoginAvailable()") {
                    Single.just(AuthCodeClient.rx.isTalkLoginAvailable(context!!))
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("authorize()") {
                    AuthCodeClient.rx.authorize(context!!)
                        .observeOn(Schedulers.io())
                        .flatMap { AuthApiClient.rx.issueAccessToken(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("authorizeWithTalk()") {
                    AuthCodeClient.rx.authorizeWithTalk(context!!, 10012)
                        .observeOn(Schedulers.io())
                        .flatMap { AuthApiClient.rx.issueAccessToken(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.Header("User API"),
                ApiAdapter.Item.ApiItem("me()") {
                    UserApiClient.rx.me()
                        .flatMap {
                            val requiredScopes = mutableListOf<String>()
                            val account = it.kakaoAccount
                            if (account?.profileNeedsAgreement == true) requiredScopes.add("profile")
                            if (account?.emailNeedsAgreement == true) requiredScopes.add("account_email")
                            if (account?.legalNameNeedsAgreement == true) requiredScopes.add("legal_name")
                            if (account?.legalBirthDateNeedsAgreement == true) requiredScopes.add("legal_birth_date")
                            if (account?.legalGenderNeedsAgreement == true) requiredScopes.add("legal_gender")
                            if (account?.phoneNumberNeedsAgreement == true) requiredScopes.add("phone_number")
                            if (account?.ciNeedsAgreement == true) requiredScopes.add("account_ci")
                            if (requiredScopes.isEmpty()) {
                                Single.just(it)
                            } else {
                                Single.error<User>(ApiError.fromScopes(requiredScopes))
                            }
                        }
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            showSnackbar(it)
                        }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("accessTokenInfo()") {
                    UserApiClient.rx.accessTokenInfo()
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("shippingAddresses()") {
                    UserApiClient.rx.shippingAddresses()
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("serviceTerms()") {
                    UserApiClient.rx.serviceTerms()
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("logout()") {
                    UserApiClient.rx.logout()
                        .subscribeOn(Schedulers.io())
                        .subscribe(onCompletableSuccess, onError).addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("unlink()") {
                    UserApiClient.rx.unlink()
                        .subscribeOn(Schedulers.io())
                        .subscribe(onCompletableSuccess, onError).addTo(disposables)
                },
                ApiAdapter.Item.Header("KakaoTalk API"),
                ApiAdapter.Item.ApiItem("profile()") {
                    TalkApiClient.rx.profile()
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("sendCustomMemo()") {
                    TalkApiClient.rx.sendCustomMemo(templateIds["customMemo"] as Long)
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe(onCompletableSuccess, onError).addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("sendDefaultMemo()") {
                    TalkApiClient.rx.sendDefaultMemo(defaultFeed)
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe(onCompletableSuccess, onError).addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("sendScrapMemo()") {
                    TalkApiClient.rx.sendScrapMemo("https://developers.kakao.com")
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe(onCompletableSuccess, onError).addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("friends()") {
                    TalkApiClient.rx.friends()
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("sendCustomMessage()") {
                    TalkApiClient.rx.friends()
                        .flatMap {
                            FriendsActivity.startForResult(
                                context,
                                it.elements.map {
                                    PickerItem(
                                        it.uuid,
                                        it.profileNickname,
                                        it.profileThumbnailImage
                                    )
                                })
                        }
                        .observeOn(Schedulers.io())
                        .flatMap {
                            TalkApiClient.rx.sendCustomMessage(
                                it,
                                templateIds["customMessage"] as Long
                            )
                        }
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("sendDefaultMessage()") {
                    TalkApiClient.rx.friends()
                        .flatMap {
                            FriendsActivity.startForResult(
                                context,
                                it.elements.map {
                                    PickerItem(
                                        it.uuid,
                                        it.profileNickname,
                                        it.profileThumbnailImage
                                    )
                                })
                        }
                        .observeOn(Schedulers.io())
                        .flatMap { TalkApiClient.rx.sendDefaultMessage(it, defaultFeed) }
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("sendScrapMessage()") {
                    TalkApiClient.rx.friends()
                        .flatMap {
                            FriendsActivity.startForResult(context, it.elements.map {
                                PickerItem(
                                    it.uuid,
                                    it.profileNickname,
                                    it.profileThumbnailImage
                                )
                            })
                        }
                        .observeOn(Schedulers.io())
                        .flatMap {
                            TalkApiClient.rx.sendScrapMessage(
                                it,
                                "https://developers.kakao.com"
                            )
                        }
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("channels()") {
                    TalkApiClient.rx.channels()
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("addChannelUrl()") {
                    Single.just(TalkApiClient.rx.addChannelUrl("_ZeUTxl"))
                        .doOnSuccess { KakaoCustomTabsClient.openWithDefault(context!!, it) }
                        .subscribe({}) { error -> showSnackbar(error) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("channelChatUrl()") {
                    Single.just(TalkApiClient.rx.channelChatUrl("_ZeUTxl"))
                        .doOnSuccess { KakaoCustomTabsClient.openWithDefault(context!!, it) }
                        .subscribe({}) { error -> showSnackbar(error) }.addTo(disposables)
                },
                ApiAdapter.Item.Header("KakaoStory API"),
                ApiAdapter.Item.ApiItem("isStoryUser()") {
                    StoryApiClient.rx.isStoryUser()
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar("isStoryUser: $it") }) { showSnackbar(it) }
                        .addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("profile()") {
                    StoryApiClient.rx.profile()
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("stories()") {
                    StoryApiClient.rx.stories()
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("story()") {
                    StoryApiClient.rx.stories()
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .flatMap {
                            if (it.isNotEmpty()) {
                                StoryApiClient.rx.story(it[0].id)
                            } else {
                                Single.error<KakaoSdkError>(
                                    ClientError(
                                        ClientErrorCause.IllegalState,
                                        "No stories"
                                    )
                                )
                            }
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("deleteStory()") {
                    StoryApiClient.rx.stories()
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .flatMapCompletable {
                            if (it.isNotEmpty()) {
                                StoryApiClient.rx.deleteStory(it[0].id)
                            } else {
                                Completable.error(
                                    ClientError(
                                        ClientErrorCause.IllegalState,
                                        "No stories"
                                    )
                                )
                            }
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar("API succeeded.") }) { showSnackbar(it) }
                        .addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("linkInfo()") {
                    StoryApiClient.rx.linkInfo("https://www.kakaocorp.com")
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("postNote()") {
                    StoryApiClient.rx.postNote("Posting note from Kakao SDK Sample.")
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("postLink()") {
                    StoryApiClient.rx.linkInfo("https://www.kakaocorp.com")
                        .flatMap {
                            StoryApiClient.rx.postLink(it, "Posting link from Kakao SDK Sample.")
                                .retryWhen(
                                    TokenBasedApiInterceptor.instance.dynamicAgreement(
                                        context!!
                                    )
                                )
                        }
                        .retryWhen(TokenBasedApiInterceptor.instance.dynamicAgreement(context!!))
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.Header("KakaoLink API"),
                ApiAdapter.Item.ApiItem("isKakaoLinkAvailable()") {
                    Single.just(LinkClient.rx.isKakaoLinkAvailable(context = context!!))
                        .subscribe({ showSnackbar("isKakaoLinkAvailable: $it") }) { showSnackbar(it) }
                        .addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("customTemplate()") {
                    LinkClient.rx.customTemplate(context!!, templateIds["customMemo"] as Long)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess { startActivity(it) }
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar("API succeeded.") }) { showSnackbar(it) }
                        .addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("scrapTemplate()") {
                    LinkClient.rx.scrapTemplate(context!!, "https://developers.kakao.com")
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess { startActivity(it) }
                        .subscribeOn(Schedulers.io())
                        .subscribe({}) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("defaultTemplate()") {
                    LinkClient.rx.defaultTemplate(context!!, defaultFeed)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess { startActivity(it) }
                        .subscribeOn(Schedulers.io())
                        .subscribe({}) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("defaultTemplate() - list") {

                },
                ApiAdapter.Item.ApiItem("defaultTemplate() - location") {
                    LinkClient.rx.defaultTemplate(context!!, defaultLocation)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess { startActivity(it) }
                        .subscribe(onSuccess, onError).addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("defaultTemplate() - commerce") {
                    LinkClient.rx.defaultTemplate(context!!, defaultCommerce)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess { startActivity(it) }
                        .subscribe(onSuccess, onError).addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("defaultTemplate() - text") {
                    LinkClient.rx.defaultTemplate(context!!, defaultText)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess { startActivity(it) }
                        .subscribe(onSuccess, onError).addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("customTemplateUri() - web sharer") {
                    Single.just(WebSharerClient.instance.customTemplateUri(templateIds["customMemo"] as Long))
                        .doOnSuccess { KakaoCustomTabsClient.openWithDefault(context!!, it) }
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)

                },
                ApiAdapter.Item.ApiItem("scrapTemplateUri() - web sharer") {
                    Single.just(WebSharerClient.instance.scrapTemplateUri("https://developers.kakao.com"))
                        .doOnSuccess { KakaoCustomTabsClient.openWithDefault(context!!, it) }
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("defaultTemplateUri() - web sharer") {
                    Single.just(WebSharerClient.instance.defaultTemplateUri(defaultFeed))
                        .doOnSuccess { KakaoCustomTabsClient.openWithDefault(context!!, it) }
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("defaultTemplateUri() - location with web sharer") {
                    Single.just(WebSharerClient.instance.defaultTemplateUri(defaultLocation))
                        .doOnSuccess { KakaoCustomTabsClient.openWithDefault(context!!, it) }
                        .subscribe(onSuccess, onError).addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("uploadImage()") {
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.sample1)
                    val file = File(context!!.cacheDir, "sample1.png")
                    val stream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.flush()
                    stream.close()
                    LinkClient.rx.uploadImage(file)
                        .subscribeOn(Schedulers.io())
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.Header("KakaoNavi API"),
                ApiAdapter.Item.ApiItem("isKakaoNaviInstalled()") {
                    Single.just(NaviClient.instance.isKakaoNaviInstalled(context!!))
                        .subscribe({ showSnackbar("isKakaoNaviInstalled: $it") }) { showSnackbar(it) }
                        .addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("shareDestinationIntent()") {
                    Single.just(
                        NaviClient.instance.shareDestinationIntent(
                            Location("카카오 판교 오피스", 127.10821222694533, 37.40205604363057),
                            NaviOptions(coordType = CoordType.WGS84)
                        )
                    )
                        .doOnSuccess { startActivity(it) }
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("navigateIntent()") {
                    Single.just(
                        NaviClient.instance.navigateIntent(
                            Location("카카오 판교 오피스", 127.10821222694533, 37.40205604363057),
                            NaviOptions(coordType = CoordType.WGS84)
                        )
                    )
                        .doOnSuccess { startActivity(it) }
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                },
                ApiAdapter.Item.ApiItem("shareDestinationUri()") {
                    Single.just(
                        NaviClient.instance.navigateUrl(
                            Location("카카오 판교 오피스", 127.10821222694533, 37.40205604363057),
                            NaviOptions(coordType = CoordType.WGS84)
                        )
                    )
                        .doOnSuccess { KakaoCustomTabsClient.openWithDefault(context!!, it) }
                        .subscribe({ showSnackbar(it) }) { showSnackbar(it) }.addTo(disposables)
                }
            )
        )
        recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            templateIds = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_open, container, false)
//        setHasOptionsMenu(true)
        return view
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_main, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//
//    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    private lateinit var templateIds: Bundle

    companion object {
        fun newInstance(templateIds: Bundle): OpenFragment =
            OpenFragment().apply {
                arguments = templateIds
            }

        val defaultFeed = FeedTemplate(
            content = Content(
                "딸기 치즈 케익",
                "http://mud-kage.kakao.co.kr/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
                Link(
                    "https://developers.kakao.com",
                    "https://developers.kakao.com"
                ),
                description = "#케익 #딸기 #삼평동 #카페 #분위기 #소개팅"
            ),
            social = Social(),
            buttons = listOf(
                Button(
                    "웹으로 보기",
                    Link(
                        "https://developers.kakao.com",
                        "https://developers.kakao.com"
                    )
                ),
                Button(
                    "앱으로 보기",
                    Link(
                        "https://developers.kakao.com",
                        "https://developers.kakao.com",
                        mapOf("key1" to "value1", "key2" to "value2"),
                        mapOf("key1" to "value1", "key2" to "value2")
                    )
                )
            )
        )

//        val defaultList = ListTemplate(
//
//        )

        val defaultLocation = LocationTemplate(
            content = Content(
                title = "카카오톡 링크",
                description = "디폴트 템플릿 Location",
                imageUrl = "http://alpha-api1-kage.kakao.com/dn/cerDB5/ZSb2iRugKx/M4nuZxX823tnK1Mk5yVcv0/kakaolink40_original.png",
                link = Link(
                    webUrl = "https://developers.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            ),
            social = Social(likeCount = 100, commentCount = 200),
            buttonTitle = "바로 확인",
            address = "경기 성남시 분당구 판교역로 235 에이치스퀘어 N동 7층",
            addressTitle = "카카오"
        )

        val defaultCommerce = CommerceTemplate(
            content = Content(
                title = "카카오톡 링크",
                description = "디폴트 템플릿 Commerce",
                imageUrl = "http://alpha-api1-kage.kakao.com/dn/cerDB5/ZSb2iRugKx/M4nuZxX823tnK1Mk5yVcv0/kakaolink40_original.png",
                link = Link(
                    webUrl = "https://developers.kakao.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            ),
            commerce = Commerce(regularPrice = 10000),
            buttonTitle = "바로 확인"
        )

        val defaultText = TextTemplate(
            text = """
                카카오링크는 카카오 플랫폼 서비스의 대표 기능으로써 사용자의 모바일 기기에 설치된 카카오 플랫폼과 연동하여 다양한 기능을 실행할 수 있습니다.
                현재 이용할 수 있는 카카오링크는 다음과 같습니다.
                카카오톡링크
                카카오톡을 실행하여 사용자가 선택한 채팅방으로 메시지를 전송합니다.
                카카오스토리링크
                카카오스토리 글쓰기 화면으로 연결합니다.
            """.trimIndent(),
            link = Link(
                webUrl = "http://developers.kakao.com",
                mobileWebUrl = "http://dev.kakao.om"
            )
        )

    }
}
