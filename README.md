# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* 

### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 


# 1주차 - 3장 구현
...
# 2주차 - 5장 리팩토링
AS-IS: 
- HttpMessageReader 
  - 역할이 너무 많다. 
    1. Http Request Message parsing
    2. Http Request DTO
  - 이름이 부적절하다. 
    - HTTP Message 중 Request Message 만 readable 하다.
    - Read 후 결과 값을 저장하고 있다.
  - URL만으로 요청을 구분한다.
  
TO-BE:
- RequestHandler 
  - 요청부터 응답까지 순서를 보장한다.
- HttpMessageReader -> HttpRequestMessageParser 
  - InputStream를 파싱하여 HttpRequestMessage로 리턴
- HttpMessageReader > HttpRequest 
  - version, method, url path, header, body 를 필드로 가지는 DTO
- RequestHandler.response**() -> HttpResponse 
  - version, status code, message, header, body 를 필드로 가지는 DTO
- HttpStatusCode 
  - code 와 code description 을 가지는 enum
- ResponseServiceFactory
  - URL과 HTTP Method에 맞는 ResponseService 구현체 클래스를 생성해준다. (여기가 Controller의 역할)
- ResponseService
  - 비즈니스 로직을 수행하는 클래스. HttpResponse를 리턴하는 인터페이스를 구현한다.
  - 로그인, 회원가입 등 요청마다 하나의 클래스를 정의한다
- HttpResponseWriter
  - OutputStream과 HttpResponse와 함께 생성한다.
  - OutputStream에 HttpResponse를 write한다.
- 아쉬운 점
  - 테스트 코드가 부족해서 리팩토링하기 어려웠다.
    - RequestHandler 테스트 하기가 어렵다.
      - Service 클래스를 테스트해서 대체할 수 있을 것 같다.
  - 예외처리가 안되어 있다.
    - RequestHandler에서 명시적으로 예외들을 처리하면 좋을 것 같다.
  - 로깅
    - Req, Res 를 모두 로깅하면 좋을 것 같다.
  - Service 구현체에 Response 쓸 때 반복코드 발생