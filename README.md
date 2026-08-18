# 손전등 (Flash)

간단한 조작으로 카메라 플래시를 켜고 끌 수 있는 Android 손전등 앱입니다. Kotlin과 Jetpack Compose로 구현했으며, 플래시 상태와 권한 상태를 한 화면에서 명확하게 안내합니다.

![Android](https://img.shields.io/badge/Android-12%2B-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)

## 주요 기능

- 중앙 전원 버튼을 통한 플래시 켜기 및 끄기
- 카메라 권한 요청과 영구 거부 시 앱 설정 화면 연결
- 후면 카메라 플래시 우선 탐색 및 다른 플래시 카메라 대체 선택
- 다른 앱의 카메라 사용, 플래시 미탑재, 접근 오류 상태 안내
- 시스템 플래시 상태 변경을 감지해 화면 상태 동기화
- 화면 높이에 맞춘 반응형 레이아웃과 접근성 상태 설명
- 화면 너비에 맞는 AdMob 적응형 배너

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| 언어 | Kotlin 2.2.10 |
| UI | Jetpack Compose, Material 3 |
| 하드웨어 제어 | Android Camera2 API |
| 광고 | Google Mobile Ads SDK |
| 빌드 | Gradle 9.4.1, Android Gradle Plugin 9.2.1 |
| Java 도구 체인 | Java 21 |

## 실행 환경

- Android Studio
- Android SDK 37
- JDK 21
- Android 12(API 31) 이상의 플래시 지원 기기

실제 플래시 동작은 하드웨어가 필요하므로 에뮬레이터보다 실제 Android 기기에서 확인하는 것을 권장합니다.

## 시작하기

```bash
git clone https://github.com/chlqudco/Flash.git
cd Flash
```

Android Studio에서 프로젝트를 열고 Gradle 동기화가 끝난 뒤 `app` 구성을 실행합니다. 처음 플래시를 켤 때 표시되는 카메라 권한 요청을 허용해야 합니다.

## 빌드 및 테스트

Windows에서는 다음 명령을 사용합니다.

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

macOS 또는 Linux에서는 `gradlew.bat` 대신 `./gradlew`를 사용합니다.

디버그 APK는 빌드 후 `app/build/outputs/apk/debug/app-debug.apk`에 생성됩니다.

## 프로젝트 구조

```text
app/src/main/
├── java/com/chlqudco/flash/
│   ├── MainActivity.kt              # 권한, 카메라 및 플래시 상태 관리
│   ├── ads/AdMobBanner.kt           # 적응형 배너 광고
│   └── ui/
│       ├── FlashlightScreen.kt      # Compose 손전등 화면
│       └── theme/                   # 색상, 서체 및 테마
├── res/                             # 문자열, 테마 및 앱 아이콘
└── AndroidManifest.xml              # 권한과 앱 구성
```

## 권한

Android의 플래시 제어 API를 사용하기 위해 `CAMERA` 권한을 요청합니다. 앱은 카메라로 사진이나 동영상을 촬영하거나 저장하지 않습니다.

## 광고 설정

AdMob 앱 ID와 배너 ID는 [`app/src/main/res/values/strings.xml`](app/src/main/res/values/strings.xml)에서 관리합니다. 개발 및 테스트 중에는 Google에서 제공하는 테스트 광고 ID를 사용하고 실제 광고를 클릭하지 마세요.
