# 사용법
### 0. git clone 
### 1. application.yml 파일 생성

```
spring:
  application:
    name: ZyptApiServer

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/livekit
    username: 본인의 mysql username
    password: 비밀번호



  jpa:
    properties:
      hibernate:
        show_sql: true
        format_sql: true
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: none
      # OSIV OFF
    open-in-view: false

  # redis config
  data:
    redis:
      host: localhost
      port: 6379


livekit:
  api-key: 라이브킷 api 키
  secret-key: 시크릿 키
  URL: 자신의 api url


jwt :
  SECRET_KEY : 임의의 문자열 32자리
  ACCESS_TOKEN_EXPIRATION : 43200000    # 12hour milliseconds
  REFRESH_TOKEN_EXPIRATION : 1209600000 # 14days


```
### 2. docker 설치
### 3. docker에 redis 설치
### 4. 스프링 부트 실행
