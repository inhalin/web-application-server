# 실습을 위한 개발 환경 세팅

* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트

* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8081으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리

* 구현 단계에서는 각 요구사항을 구현하는데 집중한다.
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다.

### 요구사항 1 - http://localhost:8081/index.html로 접속시 응답

* BufferedReader로 헤더 정보를 전부 받는다.
* 첫번째 라인에서 HTTP header 정보를 받아서 요청 url만 가져온다.
  * ex) GET /index.html HTTP/1.1
  * String[] tokens = header.split(" "); // tokens[1] => /index.html
* 개별 함수를 만들어서 url 정보 파싱해오도록 구현
  * url 길이가 1보다 길면 url을 반환, 아니면 null 반환

### 요구사항 2 - get 방식으로 회원가입

* `user/form.html`로 회원가입 페이지 받아와서 폼 요청 하면 쿼리 스트링으로 정보가 넘겨진다.
* 넘어간 정보가 HTTP header에 있고, `?`를 기준으로 나눠준다.
  * `?`를 기준으로 앞부분이 `url`, 뒷부분이 `params`
  * 각각 따로 가져오도록 private 메서드로 `getUrl()`, `getParams()` 구현

### 요구사항 3 - post 방식으로 회원가입

* `form.html`에서 method를 post로 변경
* `getParams()`가 String을 반환하도록 변경
* while문 돌면서 문자열에 `Content-Length`가 포함된 경우에는 contentLength를 저장
  * while문 안에서 IOUtils.readData() 하면 안됨 -> 이상한 값을 읽어옴
* while문 끝나고 밖에서 contentLength가 0보다 크면 readData해서 쿼리스트링을 파싱
* 파싱한 값이 있으면 넘어온 회원가입 정보가 있는 것이므로 User 객체 생성

### 요구사항 4 - redirect 방식으로 이동

* 302 redirect 스펙 확인해서 작업하기

Client request:
```
GET /index.html HTTP/1.1
Host: localhost:8081
```

Server response:
```
HTTP/1.1 302 Found
Location: /index.html
```

### 요구사항 5 - cookie

* 회원가입 유저 정보 저장
  * `/user/create` - Database.addUser() 로 디비 저장
* 로그인
  * 실패시 `redirect:/user/login_failed.html`
  * 성공시 쿠키에 `loggedIn=true` 설정해주고 `redirect:/index.html`
* 함수 묶는 부분에서 개선 필요. 302 redirect 부분이 중복된다.

### 요구사항 6 - 사용자 목록 출력

* 쿠키에 `loggedin=true` 값이 설정되어 있는 경우 `/user/list`에서 사용자 목록 전부 출력
* 추가사항: 로그아웃 기능 구현 필요

### 요구사항 7 - CSS 지원하기

* request url이 `.css`로 끝나는 경우 Content-Type을 `text/css`로 지정

# 리팩토링

### 1단계: 요청 데이터 처리 로직을 별도 클래스로 분리

- HttpRequest.class
  - InputStream을 생성자로 받아 HTTP 메서드, URL, 헤더, 본문을 분리
  - 헤더는 Map<String, String>에 저장, getHeader("필드명") 메서드로 접근
  - GET, POST로 전달된 인자는 Map<String, String>에 저장, getParameter("인자명") 메서드로 접근