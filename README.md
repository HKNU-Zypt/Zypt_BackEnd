# 필수 설치
Docker, intelliJ

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
    username: root
    password: 1234

  flyway:
    enabled: true
    locations: classpath:db/migration


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
  api-key: 
  secret-key: 
  URL: 

jwt :
  SECRET_KEY : 
  ACCESS_TOKEN_EXPIRATION : 43200000    # 12hour milliseconds
  REFRESH_TOKEN_EXPIRATION : 1209600000 # 14days

google:
  CLIENT_ID : 

kakao:
  APP_KEY : 
  ADMIN_KEY : 

naver:
  CLIENT_ID : 
  CLIENT_SECRET : 


---

spring:
  config:
    activate:
      on-profile: dev-docker

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://mysql-db:3306/zypt
    username: root
    password: 1234

  # redis config
  data:
    redis:
      host: redis-cache-server
      port: 6379


```
키값 및 빈칸 다 입력


### 2. docker 설치
intellJ 혹은 해당 스프링 프로젝트 폴더에서 명령어 툴을 실행
```
 ./gradlew clean build -x test
```
해당 명령어로 빌드

```
docker compose up -d --build
```
컴포즈로 한번에 컨테이너 띄우기

```
docker compose down
```
컴포즈로 묶은 컨테이너 전부 종료 및 삭제 

스웨거로 API 확인

http://localhost:8080/swagger-ui/index.html#/
