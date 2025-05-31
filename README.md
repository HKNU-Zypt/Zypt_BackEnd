# 필수 설치
Java, Mysql, Redis, Docker, intelliJ

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

google:
  CLIENT_ID : 클라이언트 id 값 

```
### 2. docker 설치
### 3. docker에 redis 설치
  리눅스 실행
  
  ```
  docker pull redis
  ```

redis 컨테이너 생성  (-p : 포트포워딩 지정, --name : 컨테이너 이름 지정)
```
sudo docker run -p 6379:6379 --name [원하는 이름] redis
```

docker 컨테이너 상태 확인
```
sudo docker ps -a 
```

docker 컨테이너 실행
```
sudo docker start [컨테이너 이름]

```

\* docker reids cli 실행
```
docker exec -i -t [컨테이너 이름] redis-cli
```
  
### 4. schema.sql의 DDL sql을 mysql에서 실행
- 자동 적용할 수 있지만 실수 방지 및 예외상황 발생 방지를 위해 수동 생성

### 6. 스프링 main 실행
