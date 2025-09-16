# 🖼️ Our Album

## 🔧 Tech Stack
- Platform: Android
- Language: Kotlin
- Architecture: MVVM + Clean Architecture (data/domain/presentation 계층 분리)
- Networking: Firebase Firestore, Firebase Storage
- Asynchronous: Coroutines, Flow, callbackFlow
- DI: Dagger, Hilt
- Jetpack: Compose, Navigation, Lifecycle, ViewModel, StateFlow
- Auth: Firebase Authentication (Google 로그인 연동)

## 📌 주요 기능

- 🏠 **홈 & 갤러리 & 북마크**

  - 게시물 리스트 표시 (최신순 정렬)
  - 사용자 작성 게시물 / 북마크한 게시물 필터링
  - 로그인 상태에 따른 게시물 접근 제한
  - 게시물 카드 UI: 작성자 프로필 + 닉네임 + 제목 + 날짜 + 이미지 + 북마크
  - 북마크 토글 (실시간 반영, 권한 오류 방지)
  - 비로그인 시 로그인 안내 문구 표시

- 📝 **글 작성 (Write)**

  - 제목, 본문, 이미지 업로드
  - 반응형 UI 구성 (스마트폰/태블릿 지원)
  - 업로드 중 버튼 중복 방지 및 로딩 인디케이터 표시
  - 업로드 성공 시 폼 초기화

- 🔍 **상세 보기 & 수정/삭제**

  - 게시물 상세 화면: 제목 + 작성자 프로필 + 닉네임 + 이미지 + 본문 + 날짜
  - 댓글 기능 (ModalBottomSheet UI, 수정/삭제 지원, 실시간 댓글 수 표시)
  - 작성자만 게시물 수정/삭제 가능 (권한 체크)
  - 북마크 토글

- 👤 **마이페이지**

  - 로그인된 사용자 정보 (이름, 이메일)
  - Firebase 사용자 기반 로그아웃 처리

- 🧭 **네비게이션**

  - BottomNavigationBar 커스텀 구현 (홈, 갤러리,글쓰기, 북마크, 마이페이지)
  - 로그인 상태에 따라 시작화면(Home 또는 Login) 분기

## 🖥️ 구현 화면


