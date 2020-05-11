# Package com.kakao.sdk.common
카카오 SDK 의 모든 모듈들에 공통적으로 사용되는 패키지.

# Package com.kakao.sdk.common.model
카카오 SDK 에 공통으로 사용되는 모델들을 제공하는 패키지.

# Package com.kakao.sdk.common.util
카카오 SDK 에서 편의를 위한 helper 또는 util 클래스들을 제공하는 패키지.

# Package com.kakao.sdk.network
SDK 에 사용되는 네트워크 모듈을 제공하는 패키지.

# Package com.kakao.sdk.auth
카카오 로그인 관련 클라이언트들이 있는 패키지.

## 유저의 로그인 여부 판단

앱 구동 시 리프레시토큰의 유무로 로그인 여부를 판단할 수 있습니다.

* AccessTokenRepo

```kotlin
if (AccessTokenRepo.instance.fromCache().refreshToken == null) {
    // 액세스토큰을 갱신할 수 있는 리프레시토큰이 없음. 재로그인 필요.
} else {
    // 로그인된 상태
}
```

# Package com.kakao.sdk.auth.model
카카오 로그인 관련 OAuth API 의 요청과 응답에 사용되는 모델 클래스들이 있는 패키지.

# Package com.kakao.sdk.auth.network
로그인 기반 API 를 제공하기 위한 네트워크 클라이언트를 제공하는 패키지.

# Package com.kakao.sdk.auth.exception
카카오 OAuth API 의 에러 응답들을 wrapping 하는 예외 클래스들을 제공하는 패키지.

# Package com.kakao.sdk.user
유저 API 관련 클라이언트들이 있는 패키지.

## 

아래는 /v2/user/me API 를 호출하는 간단한 예제입니다.

```kotlin
UserApiClient.rx.me()
    .subscribe().addTo(compositeDisposable)
```

# Package com.kakao.sdk.user.model
유저 API 의 요청과 응답에 사용되는 모델 클래스들이 있는 패키지.

# Package com.kakao.sdk.talk
카카오톡 API 관련 클라이언트들이 있는 패키지.

# Package com.kakao.sdk.talk.model
카카오톡 API 의 요청과 응답에 사용되는 모델 클래스들이 있는 패키지.

# Package com.kakao.sdk.navi
카카오내비 기능 관련 클라이언트들이 있는 패키지.

# Package com.kakao.sdk.navi.model
카카오내비 기능 호출 시 파라미터를 구성하기 위한 모델 클래스들이 있는 패키지.

# Package com.kakao.sdk.story
카카오스토리 API 관련 클라이언트들이 있는 패키지.

# Package com.kakao.sdk.story.model
카카오스토리 API 의 요청과 응답에 사용되는 모델 클래스들이 있는 패키지.

# Package com.kakao.sdk.template
카카오링크 SDK 에서는 가장 많이 쓰이는 메시지 템플릿 형태를 기본 템플릿으로 정의하고 소스코드 상에서 간편하게 생성할 수 있는 인터페이스를 제공합니다.
카카오링크 SDK 에서는 기본 템플릿의 계층 구조를 효과적으로 표현하기 위하여 기본 자료형 이외에 다양한 오브젝트 클래스를 정의하고 있습니다.

# Package com.kakao.sdk.template.model
카카오링크 기본 템플릿에 사용되는 Entity 들을 포함하는 패키지.

# Package com.kakao.sdk.link
카카오링크 관련 클라이언트들이 있는 패키지.

# Package com.kakao.sdk.link.model
카카오링크 API의 응답에 사용되는 모델 클래스들이 있는 패키지.