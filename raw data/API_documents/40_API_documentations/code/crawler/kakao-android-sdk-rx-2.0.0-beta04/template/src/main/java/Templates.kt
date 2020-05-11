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
package com.kakao.sdk.template

/**
 * 카카오 SDK 의 기본 템플릿을 나타내는 인터페이스입니다.
 *
 * 카카오 SDK 에 서는 개발자사이트에서 별도 템플릿을 만들지 않고도 소스코드 레벨에서 간단하게 템플릿을 작성할 수 있도록 기본 템플릿을 제공하고 있습니다.
 * 이 모듈에서 제공되는 모든 템플릿 클래스는 이 인터페이스를 구현하고 있습니다. 생성된 템플릿으로 카카오링크, 카카오톡 메시지 전송에 활용할 수 있습니다.
 *
 * @author kevin.kang. Created on 2019-09-20..
 */
interface DefaultTemplate

/**
 * 기본 템플릿으로 제공되는 피드 템플릿 클래스
 *
 * 피드 템플릿은 하나의 컨텐츠와 하나의 기본 버튼을 가집니다. 소셜 정보를 추가할 수 있으며 임의의 버튼을 설정할 수도 있습니다.
 * 아래는 간단한 피드템플릿 생성 예제입니다.
 *
 * @property content 메시지의 내용. 텍스트 및 이미지, 링크 정보를 포함합니다.
 * @property social 댓글수, 좋아요수 등, 컨텐츠에 대한 소셜 정보입니다.
 * @property buttons 버튼 목록. 버튼 타이틀과 링크를 변경하고 싶을때, 버튼 두개를 사용하고 싶을때 사용. (최대 2개)
 * @property buttonTitle 기본 버튼 타이틀(자세히 보기)을 변경하고 싶을 때 설정. 이 값을 사용하면 클릭 시 이동할 링크는 content 에 입력된 값이 사용됩니다.
 */
class FeedTemplate(
    val content: Content,
    val social: Social? = null,
    val buttons: List<Button> = mutableListOf(),
    val buttonTitle: String? = null
) : DefaultTemplate {
    val objectType = Constants.TYPE_FEED
}

/**
 * 여러 개의 컨텐츠를 리스트 형태로 보여줄 수 있는 메시지 템플릿입니다.
 *
 * 리스트 템플릿은 메시지 상단에 노출되는 헤더 타이틀과, 컨텐츠 목록, 버튼 등으로 구성됩니다.
 * 헤더와 컨텐츠 각각의 링크를 가질 수 있습니다.
 * 피드 템플릿과 마찬가지로 하나의 기본 버튼을 가지며 임의의 버튼을 설정할 수 있습니다.
 *
 * @property headerTitle
 * @property headerLink
 * @property headerImageUrl
 * @property headerImageWidth
 * @property headerImageHeight
 * @property contents
 * @property buttons
 * @property buttonTitle
 */
data class ListTemplate(
    val headerTitle: String,
    val headerLink: Link,
    val headerImageUrl: String? = null,
    val headerImageWidth: Int? = null,
    val headerImageHeight: Int? = null,
    val contents: List<Content>? = mutableListOf(),
    val buttons: List<Button>? = mutableListOf(),
    val buttonTitle: String? = null
) : DefaultTemplate {
    val objectType = Constants.TYPE_LIST
}

/**
 * 기본 템플릿으로 제공되는 커머스 템플릿 클래스
 *
 * 커머스 템플릿은 하나의 컨텐츠와 하나의 커머스 정보, 하나의 기본 버튼을 가집니다. 임의의 버튼을 최대 2개까지 설정할 수 있습니다.
 *
 * @property content 메시지의 내용. 텍스트 및 이미지, 링크 정보를 포함합니다.
 * @property commerce 컨텐츠에 대한 가격 정보
 * @property buttons 버튼 목록. 버튼 타이틀과 링크를 변경하고 싶을때, 버튼 두개를 사용하고 싶을때 사용. (최대 2개)
 * @property buttonTitle 기본 버튼 타이틀(자세히 보기)을 변경하고 싶을 때 설정. 이 값을 사용하면 클릭 시 이동할 링크는 content 에 입력된 값이 사용됩니다.
 */
data class CommerceTemplate(
    val content: Content,
    val commerce: Commerce,
    val buttons: List<Button>? = mutableListOf(),
    val buttonTitle: String? = null
) : DefaultTemplate {
    val objectType = Constants.TYPE_COMMERCE
}

/**
 * 주소를 이용하여 특정 위치를 공유할 수 있는 메시지 템플릿입니다.
 *
 * 위치 템플릿은 지도 표시에 사용되는 주소 정보와 해당 위치를 설명할 수 있는 컨텐츠 오브젝트로 구성됩니다.
 * 왼쪽 하단에 기본 버튼, 오른쪽 하단에 지도를 보여주기 위한 위치 보기 버튼이 추가됩니다.
 * 위치 보기 버튼을 클릭하면 카카오톡 채팅방 내에서 바로 지도 화면으로 전환하여 해당 주소의 위치를 확인할 수 있습니다.
 *
 * @property address 공유할 위치의 주소. 예) 경기 성남시 분당구 판교역로 235
 * @property addressTitle 카카오톡 내의 지도 뷰에서 사용되는 타이틀. 예) 카카오판교오피스
 * @property content 위치에 대해 설명하는 컨텐츠 정보
 * @property social 댓글수, 좋아요수 등, 컨텐츠에 대한 소셜 정보
 * @property buttons 버튼 목록. 기본 버튼의 타이틀 외에 링크도 변경하고 싶을 때 설정. (최대 1개, 오른쪽 위치 보기 버튼은 고정)
 * @property buttonTitle 기본 버튼 타이틀(자세히 보기)을 변경하고 싶을 때 설정. 이 값을 사용하면 클릭 시 이동할 링크는 content에 입력된 값이 사용됩니다.
 */
data class LocationTemplate(
    val address: String,
    val content: Content,
    val addressTitle: String? = null,
    val social: Social? = null,
    val buttons: List<Button>? = mutableListOf(),
    val buttonTitle: String? = null
) : DefaultTemplate {
    val objectType = Constants.TYPE_LOCATION
}

/**
 * 텍스트형 기본 템플릿 클래스
 *
 * @property text 메시지에 들어갈 텍스트 (최대 200자)
 * @property link 컨텐츠 클릭 시 이동할 링크 정보
 * @property buttons 버튼 목록. 버튼 타이틀과 링크를 변경하고 싶을때, 버튼 두개를 사용하고 싶을때 사용. (최대 2개)
 * @property buttonTitle 기본 버튼 타이틀(자세히 보기)을 변경하고 싶을 때 설정. 이 값을 사용하면 클릭 시 이동할 링크는 content에 입력된 값이 사용됩니다.
 */
data class TextTemplate(
    val text: String,
    val link: Link,
    val buttons: List<Button> = mutableListOf(),
    val buttonTitle: String? = null
) : DefaultTemplate {
    val objectType = Constants.TYPE_TEXT
}